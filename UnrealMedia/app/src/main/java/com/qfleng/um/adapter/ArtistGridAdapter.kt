package com.qfleng.um.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.qfleng.um.ArtistDetailActivity
import com.qfleng.um.R
import com.qfleng.um.bean.ArtistMedia
import com.qfleng.um.databinding.ListGridLayoutItemBinding
import com.qfleng.um.util.MusicUtils

/**
 * Created by Duke
 * 歌手
 */
class ArtistGridAdapter constructor(val listener: (view: View, bean: ArtistMedia, position: Int, targetView: View) -> Unit)
    : RecyclerView.Adapter<ArtistGridAdapter.ViewHolder>() {


    val list: ArrayList<ArtistMedia> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vBinding = ListGridLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(vBinding, listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean: ArtistMedia = list[position]
        holder.bind(bean)
    }


    class ViewHolder(val vBinding: ListGridLayoutItemBinding, val listener: (view: View, bean: ArtistMedia, position: Int, targetView: View) -> Unit)
        : RecyclerView.ViewHolder(vBinding.root) {

        var curBean: ArtistMedia? = null

        init {
            vBinding.moreOptMenu.setOnClickListener { view ->


                val menu = PopupMenu(view.context, view)
                val adapterPosition = getAdapterPosition()
                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                    }
                    true
                }
                menu.inflate(R.menu.popup_menu_artist)
                menu.show()

            }
        }

        fun bind(am: ArtistMedia) {
            this.curBean = am


            vBinding.titleView.text = am.artistName
            vBinding.subTitleView.text = am.artistName

            ViewCompat.setTransitionName(vBinding.image, ArtistDetailActivity.TRANSITION_NAME)


            if (am.artistCover.isNotEmpty())
                vBinding.image.setImageBitmap(MusicUtils.getBitmapFromUri(itemView.context, Uri.parse(am.artistCover)))

            vBinding.root.setOnClickListener {
                listener(vBinding.root, am, bindingAdapterPosition, vBinding.image)
            }
        }

    }
}