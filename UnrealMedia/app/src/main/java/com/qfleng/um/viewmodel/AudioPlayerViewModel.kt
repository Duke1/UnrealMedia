package com.qfleng.um.viewmodel

import androidx.lifecycle.ViewModel
import com.qfleng.um.audio.lrc.NetLyric
import com.qfleng.um.audio.lrc.SearchMusicResult
import com.qfleng.um.audio.lrc.decryptBASE64
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.util.OkHttpReq
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.util.gsonObjectFrom

/**
 * Created by Duke
 */
class AudioPlayerViewModel : ViewModel() {

    fun loadLrc(mediaInfo: MediaInfo, complete: (lrc: String) -> Unit) {
//        val url = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=${mediaInfo.title}&duration=${mediaInfo.duration}"

        if (mediaInfo.title.isNullOrEmpty()) {
            return
        }

        var artist: String? = null
        try {
            if (mediaInfo.title!!.contains("-")) {
                val sp1 = mediaInfo.title!!.lastIndexOf("-") + 1
                artist = mediaInfo.title!!.substring(sp1, mediaInfo.title!!.length)
            }
        } finally {

        }

        val url = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s=${mediaInfo.title}&type=1&offset=0&total=true&limit=20"
        doAsync(
                asyncFunc = suspend {
                    var result = ""

                    val req = OkHttpReq.getChromeRequestBuilder().url(url).get().build()

                    val searchLyricResult = gsonObjectFrom(SearchMusicResult::class.java, OkHttpReq.instance.executeRequest(req)
                            ?: "")

                    if (null != searchLyricResult?.result?.songs && 200 == searchLyricResult.code) {
                        val list = searchLyricResult.result?.songs!!

                        val findSong = fun(): SearchMusicResult.Song? {
                            var song: SearchMusicResult.Song? = null
                            for (s in list) {
                                if (null == s) continue

                                if (!s.artists.isNullOrEmpty() && null != artist) {
                                    val artName = s.artists!![0]?.name ?: ""
                                    if (null != mediaInfo.artist && artName.contains(artist!!)) {
                                        song = s

                                        break
                                    }
                                } else {
                                    song = s

                                    break
                                }

                            }

                            return song
                        }

                        val song = findSong()

                        if (null != song) {


//                        val lrcUrl = "https://lyrics.kugou.com/download?ver=1&client=pc&id=${candidates.id}&accesskey=${candidates.accesskey}&fmt=lrc&charset=utf8"
                            val lrcUrl = "https://music.163.com/api/song/lyric?id=${song.id}&lv=1&kv=1&tv=-1"

                            val req2 = OkHttpReq.getChromeRequestBuilder().url(lrcUrl).get().build()
                            val kuGouLyric = gsonObjectFrom(NetLyric::class.java, OkHttpReq.instance.executeRequest(req2)
                                    ?: "")

                            result = kuGouLyric?.lrc?.lyric
                                    ?: ""//decryptBASE64(kuGouLyric?.lrc?.lyric)
                        }

                    }


                    result
                },
                observer = {
                    complete(it)
                }
        )

    }

}