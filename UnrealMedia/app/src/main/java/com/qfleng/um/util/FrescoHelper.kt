package com.qfleng.um.util

import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils

import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Created by Duke .
 */
object FrescoHelper {

    fun loadResizeImage(draweeView: SimpleDraweeView, uri: Uri, width: Int = 0, height: Int = 0, listener: BaseControllerListener<ImageInfo>? = null) {

        val requestBuilder: ImageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri)

        if (width > 0 || height > 0)
            requestBuilder.resizeOptions = ResizeOptions(width, height)

        load(draweeView, requestBuilder.build(), listener)

    }


    fun loadResizeImage(draweeView: SimpleDraweeView, uri: String, width: Int = 0, height: Int = 0) {
        if (TextUtils.isEmpty(uri)) return
        loadResizeImage(draweeView, Uri.parse(uri), width, height)
    }

    fun loadResizeImage(draweeView: SimpleDraweeView, drawableId: Int, width: Int = 0, height: Int = 0) {
        if (drawableId < 0) return
        loadResizeImage(draweeView, "res://packagename/$drawableId", width, height)
    }

    private fun load(draweeView: SimpleDraweeView, imageRequest: ImageRequest, controllerListener: BaseControllerListener<ImageInfo>? = null) {
        val controllerBuilder = Fresco.newDraweeControllerBuilder()

        if (null != controllerListener)
            controllerBuilder.controllerListener = controllerListener

        val controller = controllerBuilder
                .setOldController(draweeView.controller)
                .setImageRequest(imageRequest)
                .setAutoPlayAnimations(true)
                .build() as PipelineDraweeController
        draweeView.controller = controller
    }


    /**
     * 注意bitmap有效范围只在onNewResultImpl内有效，所以LoadCallback必须做处理，否则有异常
     *
     * @param uri
     * @param loadCallback bitmap,只在回调函数生命周期内有效
     * @see [Fresco API 说明](https://www.fresco-cn.org/docs/datasources-datasubscribers.html)
     */
    fun loadBitmap(uri: String, loadCallback: (bitmap: Bitmap) -> Unit) {
        if (TextUtils.isEmpty(uri)) return
        val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .build()
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(request, null)
        dataSource.subscribe(object : BaseBitmapDataSubscriber() {
            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
            }

            public override fun onNewResultImpl(bitmap: Bitmap?) {
                if (null != bitmap)
                    loadCallback(bitmap)
            }

        }, CallerThreadExecutor.getInstance())
    }

}
