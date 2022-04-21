package com.f08.backgroundremover.Activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.f08.backgroundremover.Util.FileDataPart
import com.f08.backgroundremover.R
import com.f08.backgroundremover.Util.Utils
import com.f08.backgroundremover.Util.VolleyFileUploadRequest
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_result.*
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException


class ResultActivity : AppCompatActivity() {
    val UPLOAD_URL = "http://136.244.95.126:8080/upload"
    val SERVER_URL  = "http://136.244.95.126:8080"

    private val KEY_IMAGE = "file"
    private var bitmap: Bitmap? = null
    private var imageData: ByteArray? = null
    private var selectedImageUri: Uri? = null
    private var resultURL:String?=null
    private var mDownloadID:Long?=null
    private var mInterstitialAd: InterstitialAd? = null

    private val STORAGE_PERMISSION_CODE = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Utils(this).showBanner()
        loadFullScreenAd()
        Utils(this).checkOfflineUpdate()
        im_close.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        Utils(this).createFileFolder()
        val it = intent.extras
        val mUri = it!!.getString("URI","").toUri()
        createImageData(mUri)
        btnSavePic.setOnClickListener {
            checkPermissionGallery()
        }
    }


    // Function to check and request permission.
    private fun checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(this@ResultActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@ResultActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        } else {
            if (Utils(this).getConfig().ads_level==2 ||Utils(this).getConfig().ads_level==3){
                showFullScreenAd()
            }
            downloadImageNew("BG_${System.currentTimeMillis()}","http://136.244.95.126:8080/"+resultURL!!)



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
                if (Utils(this).getConfig().ads_level==2 ||Utils(this).getConfig().ads_level==3){
                    showFullScreenAd()
                }
                downloadImageNew("BG_${System.currentTimeMillis()}","http://136.244.95.126:8080/"+resultURL!!)

            } else {
                //Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                MotionToast.createColorToast(this@ResultActivity,
                    "Storage Permission Denied",
                    "You should approve the storage permission to access to the app gallery",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null)
            }
        }
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
           // if (mDownloadID === id) {
            MotionToast.createColorToast(this@ResultActivity,
                "Download Manager",
                "Download Completed Successfully",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                null)
            //startActivity(Intent(this@ResultActivity, GalleryActivity::class.java))
            //finish()
            startActivity(Intent(this@ResultActivity, GalleryActivity::class.java))
            finish()
           // }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }


    private fun downloadImageNew(filename: String, downloadUrlOfImage: String) {

        try {
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("$filename.png")
                .setMimeType("image/png") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    "/BG/"+File.separator.toString() + filename + ".png"
                )
            mDownloadID = dm.enqueue(request)

            MotionToast.createColorToast(this@ResultActivity,
                "Download Manager",
                "Image download started",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                null)
        } catch (e: Exception) {
            Log.e("CHARAF1013",e.message.toString())
            MotionToast.createColorToast(this@ResultActivity,
                "Download Manager",
                "Image download failed to start.",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                null)
        }
    }


    private fun uploadImage() {
        imageData?: return
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            UPLOAD_URL,
            Response.Listener {
                val t =String(it.data)
                val urlImg = JSONObject(t).get("url").toString()
                println("response is: ${urlImg}")
                removeBackground(urlImg.toString())
            },
            Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                params[KEY_IMAGE] = FileDataPart("${System.currentTimeMillis()}.jpeg", imageData!!, "image/jpeg")
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }


    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        mProgress.visibility = View.VISIBLE
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
            uploadImage()
        }
    }


    fun removeBackground(res:String) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = " http://136.244.95.126:8080/rmbg?url=${res.substring(1, res.length)}"
        println(url)

// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                showFullScreenAd()
                // Display the first 500 characters of the response string.
                resultURL = JSONObject(response).get("url").toString()
                Glide.with(this)
                    .load("$SERVER_URL/$resultURL")
                    .into(ImgResult)
                //Picasso.get().load("$SERVER_URL/$resultURL").into(ImgResult);
                mProgress.visibility = View.GONE
                btnSavePic.visibility = View.VISIBLE
                Utils(this).saveToFirebase(resultURL!!)

                println("RemovedBackground : $SERVER_URL/$resultURL")

            },
            {
                //ON ERROR
            })

// Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(DefaultRetryPolicy(10000, 1, 1.0f))
        queue.add(stringRequest)

    }

    fun loadFullScreenAd(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,getString(R.string.admob_interstitial_ad), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("TAG", adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    fun showFullScreenAd(){
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            loadFullScreenAd()
        } else {
            loadFullScreenAd()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

}