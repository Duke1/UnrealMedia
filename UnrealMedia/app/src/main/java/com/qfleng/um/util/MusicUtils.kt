package com.qfleng.um.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.qfleng.um.BuildConfig
import com.qfleng.um.R
import com.qfleng.um.UmApp

import com.qfleng.um.bean.MediaInfo
import java.io.FileDescriptor
import java.io.FileNotFoundException

object MusicUtils {


    /**
     * 用于从数据库中查询歌曲的信息，保存在ArrayList当中
     *
     * @param context
     * @return
     */
    fun getMp3Infos(context: Context?): ArrayList<MediaInfo> {
        val musicInfos = ArrayList<MediaInfo>()

        if (null == context) return musicInfos

        val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)


        if (null != cursor) {
            while (cursor.moveToNext()) {

                val musicInfo = MediaInfo()
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) // 音乐id
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) // 音乐标题
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) // 艺术家
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)) // 专辑
                val albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()
                val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) // 时长
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)) // 文件大小
                val url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)) // 文件路径
                val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) // 是否为音乐

                if (isMusic != 0) { // 只把音乐添加到集合当中
                    musicInfo.id = id
                    musicInfo.title = title
                    musicInfo.artist = artist
                    musicInfo.album = album
                    musicInfo.albumId = albumId
                    musicInfo.duration = (duration / 1000).toInt()
                    musicInfo.size = size
                    musicInfo.url = url

                    musicInfo.cover = getCoverFromFile(id, albumId)
                    musicInfos.add(musicInfo)
                }
            }
            cursor.close()
        }

        return musicInfos
    }


    fun getCoverFromFile(songid: Long, albumid: Long): String {
        var uri: Uri
        if (albumid < 0) {
            uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart")
        } else {
            uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumid)
        }

        return uri.toString()
    }


    fun getBitmapFromUri(context: Context?, uri: Uri): Bitmap? {
        if (null == context) return null

        var bm: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                fd = pfd.fileDescriptor
            }

            options.inSampleSize = 1

            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.RGB_565

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
        } catch (e: FileNotFoundException) {
            val app = context.applicationContext as UmApp
            bm = app.rawImageLoader.loadImage(context, R.mipmap.ic_launcher)
        }

        return bm
    }


    /**
     * 格式化时间，将毫秒转换为分:秒格式
     *
     * @param time
     * @return
     */
    fun formatTime(time: Long): String {
        var min = (time / (1000 * 60)).toString() + ""
        var sec = (time % (1000 * 60)).toString() + ""
        if (min.length < 2) {
            min = "0" + time / (1000 * 60) + ""
        } else {
            min = (time / (1000 * 60)).toString() + ""
        }
        if (sec.length == 4) {
            sec = "0" + time % (1000 * 60) + ""
        } else if (sec.length == 3) {
            sec = "00" + time % (1000 * 60) + ""
        } else if (sec.length == 2) {
            sec = "000" + time % (1000 * 60) + ""
        } else if (sec.length == 1) {
            sec = "0000" + time % (1000 * 60) + ""
        }
        return min + ":" + sec.trim { it <= ' ' }.substring(0, 2)
    }
}