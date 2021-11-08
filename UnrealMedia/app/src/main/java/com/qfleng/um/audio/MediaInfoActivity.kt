package com.qfleng.um.audio

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.qfleng.um.BaseActivity
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.ActivityMediaInfoBinding
import com.qfleng.um.util.BitmapHelper
import com.qfleng.um.util.ThemeHelper
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.util.dpToPx

/**
 * 歌曲信息
 * Created by Duke
 */

class MediaInfoActivity : BaseActivity() {


    companion object {
        fun launch(activity: BaseActivity, mi: MediaInfo) {
            val intent = Intent(activity, MediaInfoActivity::class.java)
            intent.putExtra("_data", mi)
            activity.startActivity(intent)
        }

    }

    val vBinding by lazy { ActivityMediaInfoBinding.inflate(layoutInflater) }

    private val _data by lazy { intent.getParcelableExtra<MediaInfo>("_data") }

    private var oriBitmap: Bitmap? = null
    private var blurBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeHelper.setTranslucentStatusBar(this)

        setContentView(vBinding.root)

        ThemeHelper.fitsSystemWindows(vBinding.toolbarView)

        setupNavigationView()

        vBinding.infoView.movementMethod = ScrollingMovementMethod.getInstance()

        if (null != _data) {
            val metadataRetriever = MediaMetadataRetriever();
            metadataRetriever.setDataSource(_data!!.url)


            val oriBytes = metadataRetriever.embeddedPicture
            if (null != oriBytes && oriBytes.isNotEmpty()) {
                oriBitmap = BitmapFactory.decodeByteArray(oriBytes, 0, oriBytes.size)

                if (null != oriBitmap) {
                    blurBitmap = BitmapHelper.blurBitmap(this, oriBitmap!!, 3.0F, 8)
                    vBinding.bgCoverView.setImageBitmap(blurBitmap)


                    val lp = vBinding.coverView.layoutParams
                    lp.width = dpToPx(this, 100f)
                    lp.height = lp.width * (oriBitmap!!.height / oriBitmap!!.width)
                    vBinding.coverView.layoutParams = lp
                    vBinding.coverView.setImageBitmap(oriBitmap)
                }


            }

            if (null == oriBitmap) {
                vBinding.infoView.setTextColor(Color.BLACK)
            }

            vBinding.infoView.append("标题：${metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)}")
            vBinding.infoView.append("\r\n")
            vBinding.infoView.append("时长：${metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)}")
            vBinding.infoView.append("\r\n")
            vBinding.infoView.append("类型：${metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)}")
            vBinding.infoView.append("\r\n")
            vBinding.infoView.append("比特率：${metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)}")
            vBinding.infoView.append("\r\n")


        }
    }


    private fun setupNavigationView() {

        setSupportActionBar(vBinding.toolbarView)
        var ab: ActionBar? = supportActionBar
        if (null != ab) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(true)

            ab.title = _data?.title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (null != oriBitmap) {
            oriBitmap!!.recycle()
        }

        if (null != blurBitmap) {
            blurBitmap!!.recycle()
        }
    }
}
