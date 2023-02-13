package com.qfleng.um

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.qfleng.um.databinding.ActivityAboutBinding

/**
 * Created by Duke
 */

class AboutActivity : BaseActivity() {

    val vBinding by lazy { ActivityAboutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vBinding.root)

        setupNavigationView()

        vBinding.run {
            itemPlugins.setOnClickListener {
                startActivity(Intent(this@AboutActivity , PluginsInfoActivity::class.java))
            }
            itemIcons8.setOnClickListener{
                val uri = Uri.parse("https://icons8.com/")
                val intent = Intent(Intent.ACTION_VIEW , uri)
                startActivity(intent)
            }
        }
    }


    private fun setupNavigationView() {

        setSupportActionBar(vBinding.toolbarView)
        var ab: ActionBar? = supportActionBar
        if (null != ab) { //            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(true)

            ab.title = "关于"
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
