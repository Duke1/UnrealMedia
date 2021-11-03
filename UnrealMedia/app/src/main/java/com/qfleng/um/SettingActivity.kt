package com.qfleng.um

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.qfleng.um.databinding.ActivitySettingBinding

/**
 * Created by Duke
 */

class SettingActivity : BaseActivity() {

    val vBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vBinding.root)

        setupNavigationView()

    }


    private fun setupNavigationView() {

        setSupportActionBar(vBinding.toolbarView)
        var ab: ActionBar? = supportActionBar
        if (null != ab) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(true)

            ab.title = "设置"
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
