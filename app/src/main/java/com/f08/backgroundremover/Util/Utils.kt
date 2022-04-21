package com.f08.backgroundremover.Util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import com.f08.backgroundremover.BuildConfig
import com.f08.backgroundremover.Models.Picture
import com.f08.backgroundremover.Models.mConfig
import com.f08.backgroundremover.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.header.*
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    var mCtx:Context?=null
    //lateinit var mAuth: FirebaseAuth
    private lateinit var mSharedPreferences: SharedPreferences
    var RootDirectory = "/Pictures/BG/"

    var RootDirectoryShow = File(Environment.getExternalStorageDirectory().toString() +
            "/Pictures/BG/")

    constructor(mCtx:Context){
        mSharedPreferences = mCtx.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
       // mAuth = FirebaseAuth.getInstance()
        this.mCtx=mCtx
        val mAct = mCtx as Activity

        if (mAct.header_btn_back != null) {
            mAct.header_btn_back.setOnClickListener { mAct.finish() }
        }


    }
/*
    @SuppressLint("HardwareIds")
    fun getDeviceIdOrUID():String{
        if (mAuth.currentUser!=null){
            return mAuth.uid!!
        }else{
        val device_id = Settings.Secure.getString(mCtx!!.contentResolver, Settings.Secure.ANDROID_ID)
        return device_id
        }
    }
*/
    @SuppressLint("HardwareIds")
    fun getDeviceId():String{
            val device_id = Settings.Secure.getString(mCtx!!.contentResolver, Settings.Secure.ANDROID_ID)
            return device_id
    }


    fun saveUserEmail(email:String){
        val editShared = mSharedPreferences.edit()
        editShared.putString("email",email)
        editShared.apply()
    }

    fun getUserEmail():String{
        val user_role = mSharedPreferences.getString("email","default@anwi.dz")
        return user_role!!
    }

    fun isAgent():Boolean{
        return getUserEmail().contains("agent")
    }



    fun runLoading(listView:ArrayList<View>,layout:View?=null){
       val mP = (mCtx as Activity).findViewById<View>(R.id.mProgress)
        mP.visibility = View.VISIBLE
        if (layout!=null){
            layout!!.alpha = 0.7f
        }
        for (i in listView){
        i.isEnabled = false
        }
    }

    fun stopLoading(listView:ArrayList<View>,layout:View?=null){
        val mP = (mCtx as Activity).findViewById<View>(R.id.mProgress)
        mP.visibility = View.GONE
        if (layout!=null){
            layout!!.alpha = 1f
        }
        for (i in listView){
            i.isEnabled = true
        }
    }

    fun getTodayDate(): String? {
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        return df.format(Date())
    }

    fun getDateTime(): String? {
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return df.format(Date())
    }

    fun saveToFirebase(url:String){
        var mRefKey =FirebaseDatabase.getInstance().reference.child("Pictures").child(getDeviceId()).push()
        mRefKey.setValue(Picture(mRefKey.key.toString(),System.currentTimeMillis(),url))
    }

    fun createFileFolder() {
        if (!RootDirectoryShow.exists()) {
            RootDirectoryShow.mkdirs()
        }

    }

    fun shareImage(context: Context, filePath: String?) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.share_txt))
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, filePath, "", null)
            val screenshotUri = Uri.parse(path)
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            intent.type = "image/*"
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.resources.getString(R.string.share_image_via)
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            mCtx!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun showBanner(){
        val mAdView = (mCtx as Activity)!!.findViewById<AdView>(R.id.adView)
        //mAdView.adUnitId = mCtx!!.getString(R.string.admob_banner_ad)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    fun saveConfig(config:mConfig){
        val editShared = mSharedPreferences.edit()
        editShared.putInt("version",config.version!!)
        editShared.putString("update_features",config.update_features)
        editShared.putBoolean("force_update",config.force_update!!)
        editShared.putInt("ads_level",config.ads_level!!)
        editShared.apply()
    }

    fun getConfig():mConfig{
        val mCf = mConfig()
        mCf.version = mSharedPreferences.getInt("version",1)
        mCf.update_features = mSharedPreferences.getString("update_features","default@anwi.dz")
        mCf.force_update = mSharedPreferences.getBoolean("force_update",true)
        mCf.ads_level = mSharedPreferences.getInt("ads_level",1)
        return mCf
    }

     fun showAppUpdateDialog(config: mConfig) {
        val googlePlay = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID

        val alert = androidx.appcompat.app.AlertDialog.Builder(mCtx!!)
        alert.setTitle("New Update Available")
            .setMessage(config.update_features)
            .setPositiveButton("Update") { dialog, which ->
                    dialog.dismiss()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(googlePlay))
                    mCtx!!.startActivity(browserIntent)

                //update clicked
            }
            .setNegativeButton("Later") { dialog, which -> //exit clicked
                if (config.force_update!!.equals(true)) {
                    System.exit(0)
                }
                dialog.dismiss()
            }
        alert.setCancelable(!config.force_update!!)
        alert.create().show()
    }

    fun  getRandomBoolean():Boolean{
        val list = arrayListOf<Boolean>(true,false)
        list.shuffle()
        return list[0]
    }

    fun checkOfflineUpdate(){
        if( getConfig().version!! > BuildConfig.VERSION_CODE){
            showAppUpdateDialog(getConfig())
        }

    }




}