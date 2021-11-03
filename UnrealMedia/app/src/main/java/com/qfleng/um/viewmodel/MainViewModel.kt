package com.qfleng.um.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.bean.ArtistMedia
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.database.AppDbHelper
import com.qfleng.um.database.entity.MusicInfo
import com.qfleng.um.database.entity.StringKeyData
import com.qfleng.um.util.MusicUtils
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.util.gsonObjectFrom
import com.qfleng.um.util.toJsonString
import java.util.ArrayList

/**
 * Created by Duke
 */
class MainViewModel : ViewModel() {

    val mediaLd = MutableLiveData<ArrayList<MediaInfo>>()
    val artistMediasLd = MutableLiveData<ArrayList<ArtistMedia>>()


    fun loadMedias(ctx: Context, loadCache: Boolean = true) {

        doAsync(
                asyncFunc = suspend {

                    val result = ArrayList<MediaInfo>()
                    val list = AppDbHelper.getInstance(ctx).appDataBase.musicInfoDao().loadAll()

                    if (list.isEmpty() || !loadCache) {
                        result.addAll(MusicUtils.getMp3Infos(ctx))

                        //缓存
                        cacheMediaInfos(ctx, result)
                    } else {
                        result.clear()
                        for (mi in list) {
                            val tmp = gsonObjectFrom(MediaInfo::class.java, mi.data)
                            if (null != tmp) result.add(tmp)
                        }

                    }

                    mediaLd.postValue(result)

                    val artistMedias = ArrayList<ArtistMedia>()
                    for (mi in result) {
                        val am = ArtistMedia()
                        am.artistName = if (mi.artist.isNullOrEmpty()) "未知歌手" else mi.artist!!

                        if (am.artistCover.isEmpty())
                            am.artistCover = mi.cover ?: ""

                        val index = artistMedias.indexOf(am)
                        if (index >= 0) {
                            val tmp = artistMedias[index]
                            tmp.medias.add(mi)
                            if (tmp.artistCover.isEmpty())
                                tmp.artistCover = am.artistCover
                        } else {
                            am.medias.add(mi)
                            artistMedias.add(am)
                        }
                    }

                    if (artistMedias.isNotEmpty()) {
                        artistMediasLd.postValue(artistMedias)
                    }

                    result
                },
                observer = {
                    Log.e("", "扫描完毕--媒体文件数:${it?.size ?: 0}")
                }
        )
    }

    private fun cacheMediaInfos(ctx: Context, list: ArrayList<MediaInfo>) {
        val dao = AppDbHelper.getInstance(ctx).appDataBase.musicInfoDao()

        val result = ArrayList<MusicInfo>()
        for (mi in list) {
            val tmp = MusicInfo()
            tmp.name = mi.title ?: ""
            tmp.path = mi.url ?: ""
            tmp.data = mi.toJsonString()
            result.add(tmp)
        }

        dao.deleteAll()
        dao.insertAll(result)
    }

    @SuppressLint("CheckResult")
    fun loadLastPlayInfo(ctx: Context) {

        doAsync(
                asyncFunc = suspend {
                    val skd = AppDbHelper.getInstance(ctx).appDataBase.stringKeyDataDao().loadByKey(StringKeyData.KEY_LAST_PLAY_AUDIO_URL)
                    skd?.data ?: ""
                },
                observer = {
                    val medias = mediaLd.value
                    if (null != medias) {
                        for (mi in medias) {
                            if (it == mi.url) {
                                AudioPlayManager.INSTANCE.mediaInfoLd.postValue(mi)

                                break
                            }
                        }
                    }
                }
        )

    }
}