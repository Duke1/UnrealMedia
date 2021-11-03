package com.qfleng.um.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.ActivityNavigator
import androidx.navigation.Navigation
import com.qfleng.um.R
import com.qfleng.um.adapter.MainMediaListAdapter
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.view.RecyclerListView
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.audio.AudioPlayerActivity
import com.qfleng.um.databinding.FragmentMusicRecyclerviewBinding
import com.qfleng.um.viewmodel.MainViewModel

/**
 * Created by Duke
 */

class MainFragment : BaseFragment() {


    lateinit var adapter: MainMediaListAdapter
    lateinit var layoutManager: GridLayoutManager

    lateinit var vBinding: FragmentMusicRecyclerviewBinding

    val mainViewModel by activityViewModels<MainViewModel>()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vBinding = FragmentMusicRecyclerviewBinding.inflate(inflater, container, false)
        return vBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainViewModel.mediaLd.observe(this, Observer<ArrayList<MediaInfo>> {
            adapter.list.clear()
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()

            mainViewModel.loadLastPlayInfo(baseActivity)
        })

        adapter = MainMediaListAdapter { view, list, position, pair ->

            AudioPlayManager.INSTANCE.play(baseActivity, position, list)
//        AudioPlayerActivity.launch(activity as BaseActivity, pair)


            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), pair.first, AudioPlayerActivity.TRANSITION_PLAYER)
            val extras = ActivityNavigator.Extras.Builder()
                    .setActivityOptions(options)
                    .build()
            Navigation.findNavController(view).navigate(R.id.action_to_player, null, null, extras)
        }
        layoutManager = GridLayoutManager(activity, 3)
        vBinding.listView.layoutManager = layoutManager
        vBinding.listView.adapter = adapter

        vBinding.listView.addItemDecoration(RecyclerListView.SimpleGridItemDecoration(requireContext(), 10f, 3))

    }

}
