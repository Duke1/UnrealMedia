package com.qfleng.um

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.qfleng.um.adapter.ArtistMediaListAdapter
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.bean.ArtistMedia
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.databinding.ActivityArtistDetailBinding
import com.qfleng.um.util.BitmapHelper
import com.qfleng.um.util.FrescoHelper
import com.qfleng.um.util.ThemeHelper
import com.qfleng.um.util.coroutines.doAsync
import com.qfleng.um.view.RecyclerListView


/**
 * 歌手详情
 */
class ArtistDetailActivity : BaseActivity() {

    companion object {
        fun launch(activity: BaseActivity, artistMedia: ArtistMedia, targetView: View) {
            val intent = Intent(activity, ArtistDetailActivity::class.java)
            intent.putExtra("_data", artistMedia)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, targetView, TRANSITION_NAME)
            activity.startActivity(intent, options.toBundle())
        }

        const val TRANSITION_NAME: String = "transition_artist_detail"
    }

    private var artistMedia: ArtistMedia? = null

    private val mediaListAdapter = ArtistMediaListAdapter(fun(view: View, list: ArrayList<MediaInfo>, position: Int, targetView: View) {

        AudioPlayManager.INSTANCE.play(this, position, list)
    })

    val vBinding by lazy { ActivityArtistDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeHelper.setTranslucentStatusBar(this)

        setContentView(vBinding.root)

        ThemeHelper.fitsSystemWindows(vBinding.toolbarView)

        if (intent.hasExtra("_data")) {
            artistMedia = intent.getParcelableExtra("_data")

        }

        if (null == artistMedia)
            finish()

        setupNavigationView()


//            artistCoverView.setImageURI(artistMedia!!.artistCover)
        FrescoHelper.loadBitmap(artistMedia!!.artistCover) {
            doAsync(
                    asyncFunc = suspend {
                        BitmapHelper.blurBitmap(this, it, 3.0F, 8)
                    },
                    observer = {
                        vBinding.artistCoverView.setImageBitmap(it)
                    }
            )
        }


        vBinding.listView.adapter = mediaListAdapter
        vBinding.listView.layoutManager = LinearLayoutManager(this)
        vBinding.listView.addItemDecoration(RecyclerListView.SimpleListItemDecoration(this, 1f, color = Color.parseColor("#88B9B5B5"))
                .enableSpanLine()
                .spanEnablePlace(RecyclerListView.PLACE_MIDDLE)
                .spanLineMargin(82f, 0f, 0f, 0f))

        mediaListAdapter.list.clear()
        mediaListAdapter.list.addAll(artistMedia!!.medias)
        mediaListAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.popup_menu_artist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.share -> {
                Snackbar.make(vBinding.root, "分享", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigationView() {

        setSupportActionBar(vBinding.toolbarView)
        var ab: ActionBar? = supportActionBar
        if (null != ab) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(true)

            ab.title = artistMedia!!.artistName
        }
    }
}
