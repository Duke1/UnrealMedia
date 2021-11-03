package com.qfleng.um.audio

/**
 * FFmpeg播放器
 */
class FFAudioPlayer {

    //C++对应对象的指针
    private var native_instance: Long = 0
    private external fun nCreate(): Long
    private external fun nSetSource(ptr: Long, uri: String)
    private external fun nPlay(ptr: Long)
    private external fun nSeek(ptr: Long, pos: Long)
    private external fun nPause(ptr: Long)
    private external fun nStop(ptr: Long)
    private external fun nPosition(ptr: Long): Long
    private external fun nDuration(ptr: Long): Long
    private external fun nDestory(ptr: Long)

    init {
        native_instance = nCreate()
    }

    fun setSource(uri: String) {
        nSetSource(native_instance, uri)
    }

    fun play() {
        nPlay(native_instance)
    }

    fun pause() {
        nPause(native_instance)
    }

    fun seek(pos: Long) {
        nSeek(native_instance, pos)
    }

    fun destory() {
        nDestory(native_instance)
    }

    fun stop() {
        nStop(native_instance)
    }

    fun position(): Long {
        return nPosition(native_instance)
    }

    fun duration(): Long {
        return nDuration(native_instance)
    }

}
