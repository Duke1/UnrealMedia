package com.qfleng.um

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import com.qfleng.um.audio.FFAudioPlayer
import com.qfleng.um.audio.PlayNotificationHelper
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.bean.PlayMediaInfo
import com.qfleng.um.util.*

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.KeyEvent
import com.qfleng.um.audio.MusicUtils


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


    private val audioPlayer by lazy { FFAudioPlayer() }
    private lateinit var mSession: MediaSessionCompat
    private lateinit var playbackStateCompat: PlaybackStateCompat
    private val sysAudioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    private val focusRequest by lazy {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                    }

                    AudioManager.AUDIOFOCUS_LOSS -> {
                        pause()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    }
                }
            }
            build()
        }
    }

    var isPlaying = false

//    lateinit var curPlayMediaInfo: String

    var curPlayMediaInfo: PlayMediaInfo? = null

    private lateinit var mHandler: Handler

    init {
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> {
                        if (PlaybackStateCompat.STATE_PLAYING == playbackStateCompat.state) {
                            val curPosition = audioPlayer.position()
                            if (curPosition >= audioPlayer.duration()) {
                                skipToNext()
                            } else {
                                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, curPosition, 1.0f)
                            }
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
                synchronized(audioPlayer) {
                    val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    if (null != event && KeyEvent.ACTION_DOWN == event.action) {
                        when (event.keyCode) {
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                            KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> onPause()
                            KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                                when (playbackStateCompat.state) {
                                    PlaybackStateCompat.STATE_PLAYING -> onPause()
                                    PlaybackStateCompat.STATE_PAUSED -> onPlay()
                                }
                            }
                            else -> {
                                return super.onMediaButtonEvent(mediaButtonEvent)
                            }
                        }
                    }
                }
                return true
            }

            override fun onCustomAction(action: String, extras: Bundle) {
                synchronized(audioPlayer) {
                    when (action) {
                        CA_PLAY_LIST -> {
                            curPlayMediaInfo = gsonObjectFrom(PlayMediaInfo::class.java, extras.getString(_DATA)
                                    ?: "")
                            play()
                        }
                        else -> {
                            super.onCustomAction(action, extras)
                        }
                    }
                }
            }

            override fun onPause() {
                pause()
            }

            override fun onPlay() {
                synchronized(audioPlayer) {
                    requestAudioFocus()

                    audioPlayer.play()
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, audioPlayer.position())
                    showPlayingNotification()
                }

            }

            override fun onStop() {
                stop()
            }

            override fun onSeekTo(pos: Long) {
                synchronized(audioPlayer) {
                    audioPlayer.seek(pos)
                }
            }

            override fun onSkipToNext() {
                skipToNext()
            }

            override fun onSkipToPrevious() {
                synchronized(audioPlayer) {
                    if (null == curPlayMediaInfo) return
                    val tmpIndex = curPlayMediaInfo!!.index - 1//需要根据模式来
                    if (tmpIndex < 0) return

                    val mi = curPlayMediaInfo!!.findCurMedia(tmpIndex)

                    if (null != mi) {
                        curPlayMediaInfo!!.index = tmpIndex
                        play()
                    }
                }
            }

        })
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
    }

    private fun play() {
        val pb = curPlayMediaInfo?.findCurMedia() ?: return
        isPlaying = true

        mSession.setMetadata(createMediaMetadata(pb))

        audioPlayer.setSource(pb.url!!)
        mHandler.postDelayed({ audioPlayer.play() }, 0)


        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        mHandler.sendEmptyMessage(1)

        showPlayingNotification()

        requestAudioFocus()
    }

    private fun stop() {
        synchronized(audioPlayer) {
            audioPlayer.stop()
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
        }
    }

    private fun pause() {
        synchronized(audioPlayer) {
            audioPlayer.pause()
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            showPlayingNotification()
        }
    }

    private fun skipToNext() {
        synchronized(audioPlayer) {
            if (null == curPlayMediaInfo) return
            val tmpIndex = curPlayMediaInfo!!.index + 1//需要根据模式来
            if (tmpIndex >= curPlayMediaInfo!!.size()) return

            val mi = curPlayMediaInfo!!.findCurMedia(tmpIndex)

            if (null != mi) {
                curPlayMediaInfo!!.index = tmpIndex
                play()
            }
        }
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


    private fun requestAudioFocus() {
        sysAudioManager.abandonAudioFocusRequest(focusRequest)
        val raf = sysAudioManager.requestAudioFocus(focusRequest)
    }
}
