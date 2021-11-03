package com.qfleng.um.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Duke
 */
@Parcelize
class MediaInfo : Parcelable {
    @SerializedName(value = "id")
    var id: Long = 0

    @SerializedName(value = "title")
    var title: String? = null

    @SerializedName(value = "artist")
    var artist: String? = null

    @SerializedName(value = "album")
    var album: String? = null

    @SerializedName(value = "albumId")
    var albumId: Long = 0

    @SerializedName(value = "duration")
    var duration: Int = 0

    @SerializedName(value = "size")
    var size: Long = 0

    @SerializedName(value = "url")
    var url: String? = null

    @SerializedName(value = "cover")
    var cover: String? = null

    override fun equals(other: Any?): Boolean {
        if (other is MediaInfo) {
            return this === other || this.url == other.url
        }

        return super.equals(other)
    }

}