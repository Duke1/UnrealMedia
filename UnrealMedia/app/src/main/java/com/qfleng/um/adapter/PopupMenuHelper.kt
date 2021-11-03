package com.qfleng.um.adapter

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import com.qfleng.um.R

object PopupMenuHelper {

    fun showMusicMenu(context: Context, anchor: View) {

        val menu = PopupMenu(context, anchor)
        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {

            }
            true
        }
        menu.inflate(R.menu.popup_menu_music)
        menu.show()
    }
}