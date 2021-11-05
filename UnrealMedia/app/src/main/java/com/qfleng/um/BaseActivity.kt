package com.qfleng.um

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.lifecycle.Observer
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.bean.MediaInfo
import com.qfleng.um.fragment.AudioControlFragment


open class BaseActivity : AppCompatActivity() {
    val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun setContentView(layoutResID: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null))
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)

        setupControlView()
    }


    private fun setupControlView() {
//        val bottomSheetBehavior = BottomSheetBehavior.from<View>(playerControlLayout)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (findViewById<View>(R.id.define_audio_control_view) != null) {

            AudioPlayManager.INSTANCE.mediaInfoLd.observe(this, Observer<MediaInfo> {
                if (null != it) {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.define_audio_control_view, AudioControlFragment())
                            .commit()
                }
            })

        }

    }


    fun doOnUi(func: () -> Unit) {
        handler.post { func() }
    }

}
