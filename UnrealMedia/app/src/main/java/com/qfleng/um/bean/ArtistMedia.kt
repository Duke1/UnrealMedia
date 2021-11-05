package com.qfleng.um.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Created by Duke
 */
@Parcelize
class ArtistMedia(var artistName: String = "",
                  var artistCover: String = "",
                  var medias: ArrayList<MediaInfo> = ArrayList()) : Parcelable {


    override fun equals(other: Any?): Boolean {
        if (other is ArtistMedia) {
            return artistName.trim() == other.artistName.trim()
        }

        return super.equals(other)
    }

}