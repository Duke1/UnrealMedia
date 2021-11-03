package com.qfleng.um.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.qfleng.um.R
import com.qfleng.um.audio.AudioPlayerActivity
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.ListItemMediaInfoViewBinding
import com.qfleng.um.util.MusicUtils

/**
 * Created by Duke
 */
class ArtistMediaListAdapter constructor(val listener: (view: View, list: ArrayList<MediaInfo>, position: Int, targetView: View) -> Unit)
    : RecyclerView.Adapter<ArtistMediaListAdapter.ViewHolder>() {

    val list: ArrayList<MediaInfo> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vBinding = ListItemMediaInfoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(vBinding, fun(view: View, position: Int, targetView: View) {
            listener(view, list, position, targetView)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean: MediaInfo = list[position]
        holder.bind(bean)
    }


    class ViewHolder(val vBinding: ListItemMediaInfoViewBinding, val listener: (view: View, position: Int, targetView: View) -> Unit)
        : RecyclerView.ViewHolder(vBinding.root) {

        var curBean: MediaInfo? = null

        init {
            vBinding.moreOptMenu.setOnClickListener { view ->
                PopupMenuHelper.showMusicMenu(itemView.context, view)
            }
        }

        fun bind(mi: MediaInfo) {
            this.curBean = mi

            vBinding.titleView.text = mi.title
            vBinding.subTitleView.text = mi.artist

            ViewCompat.setTransitionName(vBinding.image, AudioPlayerActivity.TRANSITION_PLAYER)

            vBinding.image.setImageBitmap(MusicUtils.getBitmapFromUri(itemView.context, Uri.parse(mi.cover)))

            vBinding.root.setOnClickListener {
                listener(vBinding.root, bindingAdapterPosition, vBinding.image)
            }
        }

    }
}