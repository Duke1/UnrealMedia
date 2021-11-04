package com.qfleng.um.audio

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.os.storage.StorageManager
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException

import com.qfleng.um.BaseActivity
import com.qfleng.um.R
import com.qfleng.um.UmApp
import com.qfleng.um.audio.view.ILrcView
import com.qfleng.um.audio.view.LrcView
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.AudioPlayerLayoutBinding
import com.qfleng.um.util.BitmapHelper
import com.qfleng.um.util.FrescoHelper
import com.qfleng.um.util.MusicUtils
import com.qfleng.um.util.ThemeHelper
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.viewmodel.AudioPlayerViewModel
import java.text.SimpleDateFormat
import java.util.ArrayList

/**
 * 播放器控制界面
 * Created by Duke
 */

class AudioPlayerActivity : BaseActivity(), ILrcView {

    companion object {
        fun launch(activity: BaseActivity, transitionViews: Pair<View, String>) {
            val intent = Intent(activity, AudioPlayerActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionViews.first, transitionViews.second)
            activity.startActivity(intent, options.toBundle())
        }

        const val TRANSITION_PLAYER: String = "transition_player"
    }


    lateinit var mediaInfo: MediaInfo

    val audioPlayerViewModel by viewModels<AudioPlayerViewModel>()

    val vBinding by lazy { AudioPlayerLayoutBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeHelper.setTranslucentStatusBar(this)
        //             getWindow().setSharedElementEnterTransition(new Fade());
        window.sharedElementExitTransition = android.transition.Explode()

        setContentView(vBinding.root)



        initView()

        AudioPlayManager.INSTANCE.mediaInfoLd.observe(this, Observer<MediaInfo> {
            Log.e("AudioPlayManager", "AudioPlayerActivity-mediaInfoLd")
            mediaInfo = it
            showPlayInfo()
            audioPlayerViewModel.loadLrc(mediaInfo) {
                bindLrc(it)
            }
            loadCoverImage()
        })

        AudioPlayManager.INSTANCE.mediaPlayProgressLd.observe(this, Observer<Int> {

            vBinding.curTimeView.text = timeFormat(it)

            vBinding.playSeekBar.progress = it


            vBinding.playLrcView.setIndex(lrcIndex(it.toLong() * 1000))
        })

        AudioPlayManager.INSTANCE.playbackState.observe(this, Observer<Int> {
            when (it) {
                PlaybackStateCompat.STATE_PLAYING ->
                    vBinding.playAnimView.setImageResource(R.drawable.vector_drawable_play_pause)
                PlaybackStateCompat.STATE_PAUSED ->
                    vBinding.playAnimView.setImageResource(R.drawable.vector_drawable_play_start)
                PlaybackStateCompat.STATE_STOPPED ->
                    vBinding.playAnimView.setImageResource(R.drawable.vector_drawable_play_start)
            }
        })
    }

    fun loadCoverImage() {
        //var bitmapDrawable: BitmapDrawable = BitmapDrawable.createFromStream(resources.openRawResource(R.raw.b1), null) as BitmapDrawable
        //val bitmap: Bitmap? = MusicUtils.getBitmapFromUri(this, Uri.parse(mediaInfo.cover))

        if (null == mediaInfo.cover) return

        val setBlurImage = fun(inBitmap: Bitmap) {
            doAsync(
                    asyncFunc = suspend {
                        BitmapHelper.blurBitmap(this, inBitmap, 2.0F, 6)
                    },
                    observer = {
                        vBinding.mediaCoverView.setImageBitmap(it)
                    }
            )

            androidx.palette.graphics.Palette.from(inBitmap).generate(object : androidx.palette.graphics.Palette.PaletteAsyncListener {
                override fun onGenerated(palette: androidx.palette.graphics.Palette?) {
                    if (null == palette) return

                    vBinding.playLrcView.setTextColor(resources.getColor(R.color.colorPrimary), Color.GRAY)
                }

            })


        }

        FrescoHelper.loadBitmap(mediaInfo.cover!!,
                onFailure = {
                    val app = applicationContext as UmApp
                    setBlurImage(app.rawImageLoader.loadImage(this, R.mipmap.ic_launcher)!!)
                },
                loadCallback = {
                    setBlurImage(it)
                }

        )

    }

    fun initView() {


        val drawable = vBinding.playAnimView.drawable
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }


        vBinding.playSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.e("onStartTrackingTouch", "onStartTrackingTouch -- ${seekBar.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.e("onStopTrackingTouch", "onStopTrackingTouch -- ${seekBar.progress}")

                AudioPlayManager.INSTANCE.seekTo(vBinding.playSeekBar.progress.toLong())
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                Log.e("onProgressChanged","")
            }
        })


        vBinding.playAnimView.setOnClickListener {
            AudioPlayManager.INSTANCE.playOrPause()
        }

        vBinding.btnSkipToNext.setOnClickListener {
            AudioPlayManager.INSTANCE.skipToNext()
        }

        vBinding.btnSkipToPrevious.setOnClickListener {
            AudioPlayManager.INSTANCE.skipToPrevious()
        }
    }

    fun showPlayInfo() {
        handler.post {

            vBinding.curTimeView.text = timeFormat(0)
            vBinding.durationTimeView.text = timeFormat(mediaInfo.duration)
            vBinding.playSeekBar.progress = 0
            vBinding.playSeekBar.max = mediaInfo.duration
        }
    }


    lateinit var lrcInfo: LrcInfo
    var lrcLists: ArrayList<LrcRow> = ArrayList(0)


    public fun lrcIndex(currentTime: Long): Int {
        var index: Int = 0
        for (i in lrcLists.indices) {
            if (i < lrcLists.size - 1) {
                if (currentTime < lrcLists.get(i).currentTime && i == 0) {
                    index = i
                }
                if ((currentTime > lrcLists.get(i).currentTime) && currentTime < lrcLists.get(i + 1).currentTime) {
                    index = i
                }
            }
            if ((i == lrcLists.size - 1) && currentTime > lrcLists.get(i).currentTime) {
                index = i
            }
        }
        return index
    }

    private fun getStoragePath(mContext: Context, is_removale: Boolean): String? {

        val mStorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        var storageVolumeClazz: Class<*>? = null
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz!!.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            val result = getVolumeList.invoke(mStorageManager)
            val length = Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement = Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                if (is_removale == removable) {
                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }


    private fun timeFormat(time: Int): String {
        var simpleDateFormat = SimpleDateFormat("mm:ss")

        return simpleDateFormat.format(time * 1000)
    }


    override fun bindLrc(lrc: String) {
        if (lrc.isEmpty()) return

        val lrcParser = LrcParse(lrc)
        //读歌词，并将数据传给歌词信息类
        lrcInfo = lrcParser.readLrc()
        //获得歌词中的结点
        lrcLists = lrcInfo.lrcLists
        vBinding.playLrcView.setLrcList(lrcInfo)
    }


}
