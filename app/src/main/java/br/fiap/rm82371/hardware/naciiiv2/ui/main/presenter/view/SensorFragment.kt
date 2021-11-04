package br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.fiap.rm82371.hardware.naciiiv2.databinding.SensorFragmentBinding
import br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.viewModel.SensorViewModel

class SensorFragment : Fragment(), SensorEventListener {

    companion object {
    }

    private lateinit var viewModel: SensorViewModel
    private lateinit var binding: SensorFragmentBinding
    private lateinit var sensorManager : SensorManager
    private var x : Float = 0.0f
    private var y : Float = 0.0f
    private var z : Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SensorFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SensorViewModel::class.java]
        setUpSensor()
    }
    private fun setUpSensor() {
        sensorManager = activity?.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]
            binding.tvSensorX.text = "X: $x"
            binding.tvSensorY.text = "Y: $y"
            binding.tvSensorZ.text = "Z: $z"
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}