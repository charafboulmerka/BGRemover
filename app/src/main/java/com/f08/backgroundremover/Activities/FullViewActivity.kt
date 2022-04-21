package com.f08.backgroundremover.Activities

import androidx.appcompat.app.AppCompatActivity
import com.f08.backgroundremover.Adapters.ShowImagesAdapter
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.f08.backgroundremover.R
import com.f08.backgroundremover.Util.Utils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_full_view.*
import java.io.File
import java.util.*

class FullViewActivity : AppCompatActivity() {
    private var activity: FullViewActivity? = null
    private var fileArrayList: ArrayList<File>? = null
    private var Position = 0
    var showImagesAdapter: ShowImagesAdapter? = null
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_view)
        Utils(this).showBanner()
        im_close.setOnClickListener { finish() }
        val extras = intent.extras
        if (extras != null) {
            fileArrayList = intent.getSerializableExtra("ImageDataFile") as ArrayList<File>?
            Position = intent.getIntExtra("Position", 0)
        }
        initViews()
    }



    fun initViews() {
        showImagesAdapter = ShowImagesAdapter(this, fileArrayList, this@FullViewActivity)
        vp_view.setAdapter(showImagesAdapter)
        vp_view.setCurrentItem(Position)
        vp_view.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(arg0: Int) {
                Position = arg0
                println("Current position==$Position")
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(num: Int) {}
        })
        imDelete.setOnClickListener(View.OnClickListener {
            if (Utils(this).getConfig().ads_level==3){
                showFullScreenAd()
            }
            val ab = AlertDialog.Builder(
                activity!!
            )
            ab.setPositiveButton("Yes") { dialog, id ->
                val b = fileArrayList!![Position].delete()
                if (b) {
                    deleteFileAA(Position)
                }
            }
            ab.setNegativeButton("No") { dialog, id -> dialog.cancel() }
            val alert = ab.create()
            alert.setTitle("Do You Want To Delete This Picture?")
            alert.show()
        })
        imShare.setOnClickListener(View.OnClickListener {
            if (Utils(this).getConfig().ads_level==3){
                showFullScreenAd()
            }
            Utils(this).shareImage(this, fileArrayList!![Position].path)
        })

    }

    override fun onResume() {
        super.onResume()
        activity = this
    }

    fun deleteFileAA(position: Int) {
        fileArrayList!!.removeAt(position)
        showImagesAdapter!!.notifyDataSetChanged()
        //Utils.setToast(activity, resources.getString(R.string.file_deleted))
        if (fileArrayList!!.size == 0) {
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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