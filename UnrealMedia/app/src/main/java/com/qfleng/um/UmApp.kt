package com.qfleng.um

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.qfleng.um.audio.AudioPlayManager
import com.qfleng.um.util.RawImageLoader

/**
 * Created by Duke
 */

class UmApp : Application() {
    val rawImageLoader = RawImageLoader()


    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(this,
                ImagePipelineConfig.newBuilder(this)
                        .setDownsampleEnabled(true)
                        .build())
        AudioPlayManager.INSTANCE.connect(this)
    }

}
