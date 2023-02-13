package com.qfleng.um

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.qfleng.um.databinding.ActivityAboutBinding
import com.qfleng.um.databinding.ActivityPluginsInfoBinding
import com.qfleng.um.util.ThemeHelper

/**
 * Created by Duke
 */

class PluginsInfoActivity : BaseActivity() {

    val vBinding by lazy { ActivityPluginsInfoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vBinding.root)

        setupNavigationView()

        vBinding.ffmpegText.movementMethod = ScrollingMovementMethod.getInstance();
        vBinding.ffmpegText.text = FFmpeg().getFFmpegInfo()
    }


    private fun setupNavigationView() {

        setSupportActionBar(vBinding.toolbarView)
        var ab: ActionBar? = supportActionBar
        if (null != ab) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(true)

            ab.title = "插件"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
