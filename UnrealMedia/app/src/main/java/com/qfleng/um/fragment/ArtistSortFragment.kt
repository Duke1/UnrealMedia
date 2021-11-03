package com.qfleng.um.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.qfleng.um.view.RecyclerListView
import com.qfleng.um.viewmodel.MainViewModel

import com.qfleng.um.ArtistDetailActivity
import com.qfleng.um.BaseActivity
import com.qfleng.um.adapter.ArtistGridAdapter
import com.qfleng.um.bean.ArtistMedia
import com.qfleng.um.databinding.FragmentMusicRecyclerviewBinding

/**
 * 按艺术家分类
 * Created by Duke
 */

class ArtistSortFragment : BaseFragment() {


    var adapter: ArtistGridAdapter = ArtistGridAdapter(fun(view: View, bean: ArtistMedia, position: Int, targetView: View) {
        ArtistDetailActivity.launch(requireActivity() as BaseActivity, bean, targetView)

    })
    lateinit var layoutManager: GridLayoutManager

    val mainViewModel by activityViewModels<MainViewModel>()
    lateinit var vBinding: FragmentMusicRecyclerviewBinding


    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vBinding = FragmentMusicRecyclerviewBinding.inflate(inflater, container, false)
        return vBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = GridLayoutManager(activity, 3)
        vBinding.listView.layoutManager = layoutManager
        vBinding.listView.adapter = adapter

        vBinding.listView.addItemDecoration(RecyclerListView.SimpleGridItemDecoration(requireContext(), 10f, 3))


        mainViewModel.artistMediasLd.observe(this, Observer<ArrayList<ArtistMedia>> {
            adapter.list.clear()
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })

    }


}
