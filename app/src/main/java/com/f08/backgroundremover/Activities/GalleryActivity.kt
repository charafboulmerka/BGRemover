package com.f08.backgroundremover.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f08.backgroundremover.Adapters.FileListAdapter
import com.f08.backgroundremover.Interfaces.FileListClickInterface
import com.f08.backgroundremover.R
import com.f08.backgroundremover.Util.Utils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.header.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GalleryActivity : AppCompatActivity(),
    FileListClickInterface {
    private var fileArrayList: ArrayList<File>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var ViewlayoutManager: RecyclerView.LayoutManager
    private var mList = ArrayList<File>()
    private lateinit var mRef: DatabaseReference
    private lateinit var viewsList:ArrayList<View>
    private var mInterstitialAd: InterstitialAd? = null

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
       header_title.text = "Gallery"
       Utils(this).showBanner()
       ViewlayoutManager = GridLayoutManager(this, 3)
       viewAdapter = FileListAdapter(this@GalleryActivity, mList,this)
       //getAllFiles()
       recyclerView = findViewById<RecyclerView>(R.id.rv_fileList).apply {
           setHasFixedSize(true)
           ViewlayoutManager.isAutoMeasureEnabled = false
           layoutManager = ViewlayoutManager
           adapter = viewAdapter
       }
       Utils(this).checkOfflineUpdate()
    }

    override fun onResume() {
        super.onResume()
        getAllFiles()

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getAllFiles() {
        mList.clear()
        var RootDirectoryShow = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures/BG/")
        val files: Array<File> = RootDirectoryShow.listFiles()
        if (files != null) {
            files.let{ list ->
                Arrays.sort(list) {
                        f1, f2 -> f2.lastModified().compareTo(f1.lastModified())
                }
            }
            for (file in files) {
                mList!!.add(file)
                Log.e("CHARAF1013",file.toString())
            }
            //mList.reverse()
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun getPosition(position: Int, file: File?) {
        if (Utils(this).getConfig().ads_level==3){
            if (Utils(this).getRandomBoolean())
            showFullScreenAd()
        }
        val inNext = Intent(this@GalleryActivity, FullViewActivity::class.java)
        inNext.putExtra("ImageDataFile", mList)
        inNext.putExtra("Position", position)
        startActivity(inNext)
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