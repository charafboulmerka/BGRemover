package com.f08.backgroundremover.Adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.f08.backgroundremover.Interfaces.FileListClickInterface
import com.f08.backgroundremover.R
import com.f08.backgroundremover.Util.Utils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.io.File
import java.util.ArrayList

class FileListAdapter: RecyclerView.Adapter<FileListAdapter.mHolder> {
    var context: Context
    var fileArrayList: ArrayList<File>?
    var fileListClickInterface: FileListClickInterface
    private var mInterstitialAd: InterstitialAd? = null
    constructor(context: Context,fileArrayList: ArrayList<File>,fileListClickInterface: FileListClickInterface){
        this.context=context
        this.fileArrayList=fileArrayList
        this.fileListClickInterface=fileListClickInterface
        loadFullScreenAd()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_file_view,parent,false)
        return mHolder(view)
    }

    override fun onBindViewHolder(holder: mHolder, i: Int) {
        val fileItem = fileArrayList!![i]
            holder.mCard.setOnClickListener { v ->
                //val intent = Intent(context, VideoPlayerActivity::class.java)
                //intent.putExtra("PathVideo", fileItem.path.toString())
              //  context.startActivity(intent)
                if (Utils(context).getConfig().ads_level==3){
                    showFullScreenAd()
                }
            }
            Glide.with(context)
                .load(fileItem.path)
                .into(holder.mImage)

        holder.mCard.setOnClickListener(View.OnClickListener {
            fileListClickInterface.getPosition(
                i,
                fileItem
            )
        })
    }
    override fun getItemCount(): Int {
        return fileArrayList!!.size
    }

    inner class mHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        override fun onClick(v: View?) {
            Log.e("BUTTON","CLICKED")
        }
        var mCard = view.findViewById<RelativeLayout>(R.id.rl_main)
        var mImage = view.findViewById<ImageView>(R.id.pc)
        var mTitle = view.findViewById<TextView>(R.id.tv_fileName)

    }

    fun loadFullScreenAd(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context,context.getString(R.string.admob_interstitial_ad), adRequest, object : InterstitialAdLoadCallback() {
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
            mInterstitialAd?.show(context as Activity)
            loadFullScreenAd()
        } else {
            loadFullScreenAd()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
}