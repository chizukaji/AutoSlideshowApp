package jp.techacademy.chizu.autoslideshowapp

import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.Manifest
import android.net.Uri
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    val hnd = Handler()
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    var renzoku = 0
    var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        val image_uri = arrayListOf<Uri>()

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                image_uri.add(imageUri)

                Log.d("ANDROID", "URI : " + image_uri.toString())
            } while (cursor.moveToNext())
        }
        cursor.close()

        val image_kazu = image_uri.count()-1

        imageView.setImageURI(image_uri[i])

        forward_button.setOnClickListener{
            if (renzoku == 0) {

                if (i < image_kazu) {
                    i += 1
                } else {
                    i = image_kazu
                }
                imageView.setImageURI(image_uri[i])
            }
        }

        back_button.setOnClickListener {
            if (renzoku == 0) {
                if (i >= 1) {
                    i -= 1
                } else {
                    i = 0
                }
                imageView.setImageURI(image_uri[i])
            }
        }

        play_stop_button.setOnClickListener{

            if (renzoku == 0) {

                renzoku = 1

                mTimer = Timer()

                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        hnd.post {
                            if (i == image_kazu) {
                                i = 0
                            } else {
                                i += 1
                            }
                            imageView.setImageURI(image_uri[i])
                        }
                    }
                }, 2000, 2000)
            }else{
                mTimer!!.cancel()
                renzoku = 0

            }
        }
    }
}
