package com.f08.backgroundremover.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.f08.backgroundremover.BuildConfig
import com.f08.backgroundremover.Models.mConfig
import com.f08.backgroundremover.R
import com.f08.backgroundremover.Util.Utils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.*
import java.util.*
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 8


    private val ONESIGNAL_APP_ID = "c6e4ec28-7642-4d98-b933-d690ec8b0a6f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
        Utils(this).showBanner()
        // Enable verbose OneSignal logging to debug issues if needed.

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        btn.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        btn2.setOnClickListener {
            checkPermissionGallery()

        }

        privacy_policy.setOnClickListener {
            val url = Utils(this).getConfig().privacy_url
            val it = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            startActivity(it)
        }


        getConfig()
    }

    // Function to check and request permission.
    private fun checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        } else {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, GalleryActivity::class.java))
                //Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                MotionToast.createColorToast(this@MainActivity,
                    "Storage Permission Denied",
                    "You should approve the storage permission to access to the app gallery",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null)
            }
        }
    }

    fun getConfig(){
        FirebaseDatabase.getInstance().reference.child("Config")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cf = snapshot.getValue(mConfig::class.java)
                    Utils(this@MainActivity).saveConfig(cf!!)
                    if (isNeedUpdate(cf.version!!)){
                        Utils(this@MainActivity).showAppUpdateDialog(cf)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun isNeedUpdate(versionCode: Int): Boolean {

        return versionCode > BuildConfig.VERSION_CODE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            main.visibility = View.GONE
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            val  it = Intent(this, ResultActivity::class.java)
            it.putExtra("URI",uri.toString())
            startActivity(it)
            finish()
            //bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            //createImageData(uri)
            // Use Uri object instead of File to avoid storage permissions
            //img.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            MotionToast.createColorToast(this,
                "Task Cancelled",
                "Please select a picture and try again",
                MotionToastStyle.WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                null)
        }
    }








}