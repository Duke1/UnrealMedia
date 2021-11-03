package com.qfleng.um.bean

import com.google.gson.annotations.SerializedName


class PlayMediaInfo(
        @SerializedName("index") var index: Int,
        @SerializedName("list") val list: ArrayList<MediaInfo>,
) {

    fun size(): Int {
        return list.size
    }

    fun findCurMedia(pIndex: Int? = null): MediaInfo? {
        if (list.isEmpty()) return null

        return if (null == pIndex) list[index] else list[pIndex]
    }
}