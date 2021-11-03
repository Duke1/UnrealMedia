package com.qfleng.um.audio

import java.util.ArrayList

class LrcInfo {


    var title: String? = null//标题
    var artist: String? = null//歌手
    var album: String? = null//专辑名字
    var bySomeBody: String? = null//歌词制作者
    var offset: String? = null
    var language: String? = null   //语言
    var errorinfo: String? = null   //错误信息


    //保存歌词信息和时间点
    lateinit var lrcLists: ArrayList<LrcRow>
}
