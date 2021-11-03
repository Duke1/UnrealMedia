package com.qfleng.um.audio.lrc

import android.util.Base64
import com.google.gson.annotations.SerializedName
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by Duke
 */


fun decryptBASE64(str: String?): String {
    if (str == null || str.length == 0) {
        return ""
    }
    try {
        val encode = str.toByteArray(charset("UTF-8"))
        return String(Base64.decode(encode, 0, encode.size, Base64.DEFAULT), Charset.forName("UTF-8"))

    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    return ""
}


class SearchMusicResult {

    @SerializedName("code")
    var code: Int = 0


    @SerializedName("result")
    var result: Result? = null


    class Result {
        @SerializedName("songs")
        var songs: ArrayList<Song?>? = null
    }

    class Song {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("artists")
        var artists: ArrayList<Artist?>? = null
    }

    class Artist {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null
    }
}


class NetLyric {


    @SerializedName("lrc")
    var lrc: Lrc? = null

    class Lrc {

        @SerializedName("lyric")
        var lyric: String? = null

    }
}

