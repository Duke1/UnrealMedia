package com.qfleng.um.audio

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.qfleng.um.MusicService
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.bean.PlayMediaInfo
import com.qfleng.um.database.AppDbHelper
import com.qfleng.um.database.entity.StringKeyData
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.util.objectFrom
import com.qfleng.um.util.toJsonString

/**
 * Created by Duke
 */

class AudioPlayManager private constructor() {

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AudioPlayManager() }
    }


    val mediaInfoLd = MutableLiveData<MediaInfo>()
    val mediaPlayProgressLd = MutableLiveData<Int>()
    val playbackState = MutableLiveData<Int>()

    lateinit var mMediaBrowser: MediaBrowserCompat
    lateinit var ctx: Context
    lateinit var supportMediaController: MediaControllerCompat


    fun connect(ctx: Context) {
        this.ctx = ctx

        mMediaBrowser = MediaBrowserCompat(ctx, ComponentName(ctx, MusicService::class.java), mConnectionCallback, null)
        mMediaBrowser.connect()
    }

    fun disconnect() {
        mMediaBrowser.disconnect()
    }

    private val mConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            try {
                connectToSession(mMediaBrowser.sessionToken)
            } catch (e: RemoteException) {
            }

        }
    }

    @Throws(RemoteException::class)
    private fun connectToSession(token: MediaSessionCompat.Token) {
        val mediaController = MediaControllerCompat(ctx, token)

        supportMediaController = mediaController
        mediaController.registerCallback(mCallback)
        val state = mediaController.playbackState


        supportMediaController.transportControls.sendCustomAction("AABBCC", Bundle())

        playbackState.postValue(PlaybackStateCompat.STATE_NONE)
    }

    private val mCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            playbackState.postValue(state.state)
            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING ->
                    mediaPlayProgressLd.postValue(state.position.toInt())
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            if (null != metadata.bundle) {
                val media = metadata.bundle.getString(MusicService.KEY_MEDIA_INFO_STR)
                val mediaInfo: MediaInfo? = ctx.objectFrom(MediaInfo::class.java, media ?: "")

                if (null != mediaInfo) mediaInfoLd.postValue(mediaInfo!!)
            }
        }
    }

    fun mediaController(): MediaControllerCompat {
        return supportMediaController
    }

    fun skipToPrevious() {
        supportMediaController.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        supportMediaController.transportControls.skipToNext()
    }

    fun playOrPause() {
        val controls = supportMediaController.transportControls

        when (playbackState.value) {
            PlaybackStateCompat.STATE_PLAYING ->
                controls.pause()
            PlaybackStateCompat.STATE_PAUSED ->
                controls.play()
            PlaybackStateCompat.STATE_NONE, PlaybackStateCompat.STATE_STOPPED -> {
//                if (null != mediaInfoLd.value)
//                    startPlay(mediaInfoLd.value)
            }
        }
    }


    /**
     * @param checkSame 检查是否同一多媒体
     */
    fun play(ctx: Context, index: Int, list: ArrayList<MediaInfo>, checkSame: Boolean = true) {
//        val playMediaInfo = mediaInfoLd.value
//        if (checkSame && null != playMediaInfo && playMediaInfo == mediaInfo) {
//            return
//        }

        startPlay(list, index)

//        saveLastPlayAudio(ctx, mediaInfo)
    }

    private fun startPlay(list: ArrayList<MediaInfo>, index: Int) {
        val bundle = Bundle()
        bundle.putString(MusicService._DATA, PlayMediaInfo(index, list).toJsonString(true))
        supportMediaController.transportControls.sendCustomAction(MusicService.CA_PLAY_LIST, bundle)
    }

    fun seekTo(pos: Long) {
        supportMediaController.transportControls.seekTo(pos)
    }

    private fun saveLastPlayAudio(ctx: Context, mi: MediaInfo) {

        doAsync(
                asyncFunc = suspend {
                    val skd = StringKeyData()
                    skd.key = StringKeyData.KEY_LAST_PLAY_AUDIO_URL
                    skd.data = mi.url ?: ""
                    AppDbHelper.getInstance(ctx).appDataBase.stringKeyDataDao().insert(skd)
                    skd
                },
                observer = {

                },
        )
    }
}
