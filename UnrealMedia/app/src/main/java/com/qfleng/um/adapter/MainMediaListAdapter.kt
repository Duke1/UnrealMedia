package com.qfleng.um.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.qfleng.um.R
import com.qfleng.um.UmApp
import com.qfleng.um.audio.AudioPlayerActivity
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.ListGridLayoutItemBinding
import com.qfleng.um.util.FrescoHelper
import com.qfleng.um.util.MusicUtils

/**
 * Created by Duke
 */
class MainMediaListAdapter constructor(val listener: (view: View, list: ArrayList<MediaInfo>, position: Int, pair: Pair<View, String>) -> Unit)
    : RecyclerView.Adapter<MainMediaListAdapter.ViewHolder>() {


    val list: ArrayList<MediaInfo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vBinding = ListGridLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(vBinding, fun(view: View, position: Int, pair: Pair<View, String>) {
            listener(view, list, position, pair)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean: MediaInfo = list[position]
        holder.bind(bean)
    }


    class ViewHolder(val vBinding: ListGridLayoutItemBinding, val listener: (view: View, position: Int, pair: Pair<View, String>) -> Unit)
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


            vBinding.root.setOnClickListener {
                listener(vBinding.root, bindingAdapterPosition, Pair(vBinding.image, AudioPlayerActivity.TRANSITION_PLAYER))
            }


            FrescoHelper.loadBitmap(mi.cover ?: "",
                    loadCallback = {
                        vBinding.image.setBackgroundColor(Color.WHITE)
                        vBinding.image.setImageBitmap(it)
                    },
                    onFailure = {
                        val app = itemView.context.applicationContext as UmApp
                        vBinding.image.setBackgroundColor(Color.LTGRAY)
                        vBinding.image.setImageBitmap(app.rawImageLoader.loadImage(itemView.context, R.raw.music_note_white_48))
                    }
            )
        }

    }
}
