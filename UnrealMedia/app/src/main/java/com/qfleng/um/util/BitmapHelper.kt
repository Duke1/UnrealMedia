package com.qfleng.um.util

import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur


object BitmapHelper {

    /**
     * 使用 RenderScript 对图片进行高斯模糊
     *参考：https://mrfzh.github.io/2019/11/14/%E9%AB%98%E6%96%AF%E6%A8%A1%E7%B3%8A%E7%9A%84%E5%AE%9E%E7%8E%B0/
     * @param context
     * @param originImage 原图
     * @param blurRadius 模糊半径，取值区间为 (0, 25]
     * @param scaleRatio 缩小比例，假设传入 a，那么图片的宽高是原来的 1 / a 倍，取值 >= 1
     * @return
     */
    fun blurBitmap(context: Context, originImage: Bitmap, blurRadius: Float, scaleRatio: Int): Bitmap {
        require(!(blurRadius <= 0 || blurRadius > 25f || scaleRatio < 1)) { "ensure blurRadius in (0, 25] and scaleRatio >= 1" }

        // 计算图片缩小后的宽高
        val width = originImage.width / scaleRatio
        val height = originImage.height / scaleRatio

        // 创建缩小的 Bitmap
        val bitmap = Bitmap.createScaledBitmap(originImage, width, height, false)

        // 创建 RenderScript 对象
        val rs: RenderScript = RenderScript.create(context)
        // 创建一个带模糊效果的工具对象
        val blur: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // 由于 RenderScript 没有使用 VM 来分配内存，所以需要使用 Allocation 类来创建和分配内存空间
        val input: Allocation = Allocation.createFromBitmap(rs, bitmap)
        // 创建相同类型的 Allocation 对象用来输出
        val output: Allocation = Allocation.createTyped(rs, input.getType())

        // 设置渲染的模糊程度，最大为 25f
        blur.setRadius(blurRadius)
        // 设置输入和输出内存
        blur.setInput(input)
        blur.forEach(output)
        // 将数据填充到 Bitmap
        output.copyTo(bitmap)

        // 销毁它们的内存
        input.destroy()
        output.destroy()
        blur.destroy()
        rs.destroy()
        return bitmap
    }

}