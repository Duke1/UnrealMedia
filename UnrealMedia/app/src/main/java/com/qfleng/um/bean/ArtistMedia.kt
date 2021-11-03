package com.qfleng.um.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Created by Duke
 */
@Parcelize
class ArtistMedia : Parcelable {

    var artistName: String = ""
    var artistCover: String = ""
    var medias: ArrayList<MediaInfo> = ArrayList()


}