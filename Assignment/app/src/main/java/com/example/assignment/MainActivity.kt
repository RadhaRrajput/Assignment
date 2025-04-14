package com.example.assignment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.Session
import com.arthenica.ffmpegkit.ReturnCode
import com.example.assignment.databinding.ActivityMainBinding
import com.google.firebase.dataconnect.LogLevel
import java.io.File

class MainActivity : AppCompatActivity() {

    private val GALLERY = 2
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var binding: ActivityMainBinding
    private var selectedVideoUri: Uri? = null
    private var convertedVideoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectvideo.setOnClickListener {
            checkPermissionAndPickVideo()
        }
    }

    private fun checkPermissionAndPickVideo() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
        } else {
            chooseVideoFromGallery()
        }
    }
    private fun chooseVideoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, GALLERY)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseVideoFromGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedVideoUri = uri
                val extension = getFileExtension(uri)

                if (extension.equals("mp4", ignoreCase = true)) {
                    playVideo(uri)
                } else {
                    convertVideoToMp4(uri)
                }
            } ?: run {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return contentResolver.getType(uri)?.let {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
        } ?: uri.path?.substringAfterLast('.', "")
    }

    private fun convertVideoToMp4(uri: Uri) {
        val inputFile = copyUriToFile(uri, "input_video")
        val outputFile = File(filesDir, "converted_video.mp4")
        convertedVideoPath = outputFile.absolutePath

        runOnUiThread { ZZProgressBar.showProgress(this)}

        val cmd = "-y -i ${inputFile.absolutePath} ${outputFile.absolutePath}"
        FFmpegKit.executeAsync(cmd) { session: Session ->
            runOnUiThread {
                ZZProgressBar.hideProgress()
                if (session.returnCode.isValueSuccess) {
                    playVideo(Uri.fromFile(outputFile))
                } else {
                    Log.e("Error","Conversion failed")
                   // Toast.makeText(this, "Conversion failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun copyUriToFile(uri: Uri, fileName: String): File {
        val inputStream = contentResolver.openInputStream(uri)
        val outputFile = File(filesDir, "$fileName.${getFileExtension(uri)}")
        val outputStream = outputFile.outputStream()

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return outputFile
    }

    private fun playVideo(videoUri: Uri) {
        val videoView: VideoView = binding.videoPreview
        videoView.setVideoURI(videoUri)
        videoView.start()
    }
}
