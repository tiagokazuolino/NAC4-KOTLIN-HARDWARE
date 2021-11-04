package br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import br.fiap.rm82371.hardware.naciiiv2.R
import br.fiap.rm82371.hardware.naciiiv2.ui.main.device.camera.captureImage
import br.fiap.rm82371.hardware.naciiiv2.ui.main.device.camera.createImageFile
import br.fiap.rm82371.hardware.naciiiv2.ui.main.device.camera.model.ImageFileModel
import br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.viewModel.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var imageFile: ImageFileModel

    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result != null && result.resultCode ==
            Activity.RESULT_OK
        ) {
            val btNext = view?.findViewById<Button>(R.id.btNext)
            val btOpenCam = view?.findViewById<Button>(R.id.btOpenCam)
            // Pega resposta da camera em um bitmap
            val photo = BitmapFactory.decodeFile(imageFile.photoFile!!.absolutePath)
            view?.findViewById<CircleImageView>(R.id.ivPhoto)?.setImageBitmap(photo)
            btNext?.isEnabled = true
            btOpenCam?.isEnabled = false

        } else {
            Toast.makeText(view?.context, "Erro ao retornar imagem da camera", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val btNext = view.findViewById<Button>(R.id.btNext)
        btNext.isEnabled = false
        val btOpenCam = view.findViewById<Button>(R.id.btOpenCam)
        btOpenCam.setOnClickListener {
            captureImage(view.context!!, activity, callCameraActivity())
        }

        btNext.setOnClickListener {
            val locationFragment = LocationFragment()
            val fragManager: FragmentManager = this.requireActivity().supportFragmentManager
            val fragTransaction: FragmentTransaction = fragManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
            fragTransaction.replace(R.id.main_fragment, locationFragment)
            fragTransaction.addToBackStack(null);
            fragTransaction.commit()
            btNext.visibility = View.INVISIBLE
        }
    }


    // Chama a activity da camera para tirar foto
    @SuppressLint("QueryPermissionsNeeded")
    private fun callCameraActivity() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(view?.context!!.packageManager) != null) {
            // Cria o arquivo onde a foto deve ser salva e chama a camera
            try {
                imageFile = createImageFile(view?.context!!)
                if (imageFile.photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        view?.context!!,
                        "br.fiap.rm82371.hardware.naciiiv2.fileprovider",
                        imageFile.photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activityResultLauncher.launch(takePictureIntent)
                }
            } catch (ex: Exception) {
                Toast.makeText(view?.context!!, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(view?.context!!, "Erro ao abrir camera", Toast.LENGTH_SHORT).show()
        }
    }
}