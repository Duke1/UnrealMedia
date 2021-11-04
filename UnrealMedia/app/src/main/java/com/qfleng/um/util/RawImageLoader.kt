package com.qfleng.um.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache


class RawImageLoader   {
    private val mCache: LruCache<Int, Bitmap>


    init {
        val maxSize = Runtime.getRuntime().maxMemory().toInt() // 返回byte

        val cacheSize = maxSize / 1024 / 8

        mCache = object : LruCache<Int, Bitmap>(cacheSize) {
            override fun sizeOf(key: Int, value: Bitmap): Int {
                return super.sizeOf(key, value)
            }

            override fun entryRemoved(evicted: Boolean, key: Int, oldValue: Bitmap, newValue: Bitmap?) {
                super.entryRemoved(evicted, key, oldValue, newValue)
            }
        }
    }


    fun loadImage(context: Context, rid: Int): Bitmap? {
        try {
            var d = mCache.get(rid)
            if (null == d) {
                d = BitmapFactory.decodeResource(context.resources, rid)
                mCache.put(rid, d)
            }

            return d
        } catch (e: Exception) {
            return null
        }


    }
}