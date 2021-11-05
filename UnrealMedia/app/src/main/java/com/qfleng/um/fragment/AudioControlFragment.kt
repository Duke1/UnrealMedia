package com.qfleng.um.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import com.qfleng.um.viewmodel.AudioPlayerViewModel

import com.qfleng.um.BaseActivity
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.audio.AudioPlayerActivity
import com.qfleng.um.bean.MediaInfo
import com.facebook.drawee.view.SimpleDraweeView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.activityViewModels
import com.qfleng.um.R
import com.qfleng.um.databinding.ViewPlayerControlBinding
import com.qfleng.um.audio.MusicUtils


/**
 * 底部播放控制Bar
 * Created by Duke
 */
class AudioControlFragment : BaseFragment() {
    val audioPlayerViewModel by activityViewModels<AudioPlayerViewModel>()
    lateinit var vBinding: ViewPlayerControlBinding

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vBinding = ViewPlayerControlBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playManager = AudioPlayManager.INSTANCE

        ViewCompat.setTransitionName(vBinding.albumArtImageView, AudioPlayerActivity.TRANSITION_PLAYER)

        vBinding.playOrPauseBtn.setOnClickListener {
            AudioPlayManager.INSTANCE.playOrPause()
        }


        playManager.mediaInfoLd.observe(this, Observer<MediaInfo> {
            vBinding.playSeekBar.progress = 0
            vBinding.playSeekBar.max = it.duration

            vBinding.mediaName.text = it.title
            vBinding.artistName.text = it.artist


            vBinding.albumArtImageView.setImageBitmap(MusicUtils.getBitmapFromUri(baseActivity, Uri.parse(it.cover)))

        })
        playManager.mediaPlayProgressLd.observe(this, Observer<Int> {
            vBinding.playSeekBar.progress = it
        })
        playManager.playbackState.observe(this, Observer<Int> {
            when (it) {
                PlaybackStateCompat.STATE_PLAYING ->
                    vBinding.playOrPauseBtn.setImageResource(R.mipmap.ic_pause_black_36dp)
                PlaybackStateCompat.STATE_PAUSED ->
                    vBinding.playOrPauseBtn.setImageResource(R.mipmap.ic_play_arrow_black_36dp)
                PlaybackStateCompat.STATE_STOPPED ->
                    vBinding.playOrPauseBtn.setImageResource(R.mipmap.ic_play_arrow_black_36dp)
            }
        })

        rootView.setOnClickListener {
            AudioPlayerActivity.launch(activity as BaseActivity, Pair<View, String>(vBinding.albumArtImageView, AudioPlayerActivity.TRANSITION_PLAYER))
        }


        //SimpleDraweeView在共享元素动画后不显示bug
        ActivityCompat.setExitSharedElementCallback(baseActivity, object : SharedElementCallback() {

            override fun onSharedElementEnd(sharedElementNames: MutableList<String>, sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                if (sharedElements != null) {
                    for (view in sharedElements) {
                        if (view is SimpleDraweeView) {
                            view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
    }
}