package com.qfleng.um.adapter

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.qfleng.um.BaseActivity
import com.qfleng.um.R
import com.qfleng.um.UmApp
import com.qfleng.um.audio.AudioPlayerActivity
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.ListItemMediaInfoViewBinding
import com.qfleng.um.audio.MusicUtils
import com.qfleng.um.util.FrescoHelper
import com.qfleng.um.util.dpToPx

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
                if (null == curBean) return@setOnClickListener
                PopupMenuHelper.showMusicMenu(itemView.context as BaseActivity, view, curBean!!)
            }
        }

        fun bind(mi: MediaInfo) {
            this.curBean = mi


            val activity = itemView.context as BaseActivity

            vBinding.titleView.text = mi.title
            vBinding.subTitleView.text = mi.artist

            ViewCompat.setTransitionName(vBinding.image, AudioPlayerActivity.TRANSITION_PLAYER)

            vBinding.root.setOnClickListener {
                listener(vBinding.root, bindingAdapterPosition, vBinding.image)
            }


            vBinding.image.setPadding(0)
            FrescoHelper.loadBitmap(mi.cover ?: "",
                    loadCallback = {
                        activity.doOnUi {
                            vBinding.image.setBackgroundColor(Color.WHITE)
                            vBinding.image.setImageBitmap(it)
                        }
                    },
                    onFailure = {
                        activity.doOnUi {
                            val app = itemView.context.applicationContext as UmApp
                            vBinding.image.setBackgroundColor(Color.LTGRAY)
                            vBinding.image.setPadding(dpToPx(itemView.context, 8f))
                            vBinding.image.setImageBitmap(app.rawImageLoader.loadImage(itemView.context, R.raw.music_note_white_48))

                        }
                    }
            )
        }

    }
}