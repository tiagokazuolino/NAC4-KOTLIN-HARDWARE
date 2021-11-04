package br.fiap.rm82371.hardware.naciiiv2.ui.main.device.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import br.fiap.rm82371.hardware.naciiiv2.ui.main.device.camera.model.ImageFileModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Cria o arquivo de imagem no sistema
@SuppressLint("SimpleDateFormat")
fun createImageFile(context: Context): ImageFileModel {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName, /* prefixo */
        ".jpg", /* sufixo */
        storageDir /* diretório */
    )
    return ImageFileModel(image, image.absolutePath)
}

fun captureImage(context: Context, activity: FragmentActivity?, until: Unit) {
    // Verifica se tem permissão para usar a camera
    // Se tem permissão, chama activity da camera
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Se não tem permissão, pede permissão para usar camera e acessar arquivos
        if (activity != null) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
    } else {
        // Se não tem permissão, pede permissão para usar camera e acessar arquivos
        until
    }
}