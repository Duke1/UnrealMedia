package com.qfleng.um

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.qfleng.um.audio.FFAudioPlayer
import com.qfleng.um.audio.PlayNotificationHelper
import com.qfleng.um.audio.lrc.SearchMusicResult
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.bean.PlayMediaInfo
import com.qfleng.um.util.*
import androidx.media.session.MediaButtonReceiver

import android.content.Intent
import android.view.KeyEvent


/**
 * Created by Duke
 */

class MusicService : MediaBrowserServiceCompat() {

    companion object {

        const val _DATA = "_data"


        const val KEY_MEDIA_INFO_STR = "media_info_str"


        //onCustomAction -> action
        const val CA_PLAY_LIST = "ca_play_list" //播放列表{"index":0,"list":[{MediaInfo}]}
    }


    val audioPlayer by lazy { FFAudioPlayer() }
    lateinit var mSession: MediaSessionCompat
    lateinit var playbackStateCompat: PlaybackStateCompat

    var isPlaying = false

//    lateinit var curPlayMediaInfo: String

    var curPlayMediaInfo: PlayMediaInfo? = null

    private lateinit var mHandler: Handler

    init {
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> {
                        if (PlaybackStateCompat.STATE_PLAYING == playbackStateCompat.state) {
                            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, audioPlayer.position(), 1.0f)
                        }

                        mHandler.sendEmptyMessageDelayed(1, 1000)
                    }
                }
            }
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {

    }


    override fun onCreate() {
        super.onCreate()


        // Start a new MediaSession
        mSession = MediaSessionCompat(this, "MusicService")
        sessionToken = mSession.sessionToken

        playbackStateCompat = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build()
        mSession.setPlaybackState(playbackStateCompat)

        mSession.setCallback(object : MediaSessionCompat.Callback() {

            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                Log.e("onMediaButtonEvent", "")
                val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                if (null != event) {
                    Log.e("onMediaButtonEvent", "event:$event")
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                        KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {

                        }
                        else -> {
                        }
                    }
                }

                return true
            }

            override fun onCustomAction(action: String, extras: Bundle) {
                when (action) {
                    CA_PLAY_LIST -> {
                        curPlayMediaInfo = gsonObjectFrom(PlayMediaInfo::class.java, extras.getString(_DATA)
                                ?: "")
                        play()
                    }
                }
            }

            override fun onPause() {
                audioPlayer.pause()

                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }

            override fun onPlay() {
                audioPlayer.play()
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, audioPlayer.position())
            }

            override fun onStop() {
                audioPlayer.stop()
                updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                audioPlayer.seek(pos)
            }

            override fun onSkipToNext() {
                Log.e("onSkipToNext", "${curPlayMediaInfo!!.toJsonString(true)}")
                if (null == curPlayMediaInfo) return
                var tmpIndex = curPlayMediaInfo!!.index + 1//需要根据模式来
                if (tmpIndex > curPlayMediaInfo!!.size()) return

                val mi = curPlayMediaInfo!!.findCurMedia(tmpIndex)

                if (null != mi) {
                    curPlayMediaInfo!!.index = tmpIndex
                    play()
                }
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
            }

        })
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
    }

    private fun play() {

        val pb = curPlayMediaInfo?.findCurMedia() ?: return
        isPlaying = true

        audioPlayer.setSource(pb.url!!)
        mHandler.postDelayed({ audioPlayer.play() }, 0)

        mSession.setMetadata(createMediaMetadata(pb))
        showPlayingNotification()

        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        mHandler.sendEmptyMessage(1)
    }

    private fun createMediaMetadata(mi: MediaInfo): MediaMetadataCompat {
        val builder = MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, mi.title)
                .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, mi.artist)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, MusicUtils.getBitmapFromUri(this, Uri.parse(mi.cover)))
                .putString(KEY_MEDIA_INFO_STR, mi.toJsonString(true))


        return builder.build()
    }

    fun updatePlaybackState(@PlaybackStateCompat.State state: Int, position: Long = 0, playbackSpeed: Float = 1.0f) {
        playbackStateCompat = PlaybackStateCompat.Builder()
                .setState(state, position, playbackSpeed)
                .build()
        mSession.setPlaybackState(playbackStateCompat)
    }

    override fun onDestroy() {
        super.onDestroy()

        audioPlayer.destory()

        PlayNotificationHelper.deleteNotificationChannel(this)
    }

    private fun showPlayingNotification() {
        val builder = PlayNotificationHelper.generateNotification(this, mSession)

        NotificationManagerCompat.from(this).notify(1, builder.build())
    }
}
