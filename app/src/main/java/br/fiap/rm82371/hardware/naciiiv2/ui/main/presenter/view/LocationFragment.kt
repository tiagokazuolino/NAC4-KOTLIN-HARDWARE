package br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import br.fiap.rm82371.hardware.naciiiv2.R
import br.fiap.rm82371.hardware.naciiiv2.databinding.LocationFragmentBinding
import br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.viewModel.LocationViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = LocationFragment()
    }

    private lateinit var viewModel: LocationViewModel
    private lateinit var binding: LocationFragmentBinding
    private lateinit var location: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LocationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        val btLocation = binding.btLocation
        val btNextToSensor = binding.btNextToSensor
        val mapFragment = childFragmentManager.findFragmentById(R.id.mvMap) as SupportMapFragment

        btLocation.isEnabled = false
        btNextToSensor.isEnabled = false
        mapFragment.getMapAsync(this)
        btLocation.setOnClickListener {
            mapFragment.getMapAsync {
                val currentLocation = LatLng(location.latitude, location.longitude)
                it.clear()
                it.addMarker(
                    MarkerOptions()
                        .position(currentLocation)
                        .title("its here!")
                )
                it.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                it.setMinZoomPreference(14.0f)
                it.setMaxZoomPreference(14.0f)
            }
            btLocation.isEnabled = false
            btNextToSensor.isEnabled = true
        }
        btNextToSensor.setOnClickListener {
            val sensorFragment = SensorFragment()
            val fragManager: FragmentManager = this.requireActivity().supportFragmentManager
            val fragTransaction: FragmentTransaction = fragManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
            fragTransaction.replace(R.id.main_fragment, sensorFragment)
            fragTransaction.addToBackStack(null);
            fragTransaction.commit()
        }
        if (hasLocationPermission(view.context!!).not()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLastLocation()
            btLocation.isEnabled = true
        }

    }

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            val latLon = "${location.latitude}, ${location.longitude} : ${location.altitude}"
            binding.tvLocation.text = latLon
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            locationClient.lastLocation.addOnCompleteListener { task ->
                location = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    val latLon =
                        "${location.latitude}, ${location.longitude} : ${location.altitude}"
                    binding.tvLocation.text = latLon
                }
            }
        } else {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()
            } else {
                Toast.makeText(view?.context!!, "Permissao negada!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

    }

    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }

}