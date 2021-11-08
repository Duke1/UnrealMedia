package com.qfleng.um.adapter

import android.view.View
import android.widget.PopupMenu
import com.qfleng.um.BaseActivity
import com.qfleng.um.R
import com.qfleng.um.audio.MediaInfoActivity
import com.qfleng.um.bean.MediaInfo

object PopupMenuHelper {

    fun showMusicMenu(activity: BaseActivity, anchor: View, mi: MediaInfo) {

        val menu = PopupMenu(activity, anchor)
        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.musicInfo -> {
                    MediaInfoActivity.launch(activity, mi)
                }

            }
            true
        }
        menu.inflate(R.menu.popup_menu_music)
        menu.show()
    }
}