package com.qfleng.um.util

import okhttp3.OkHttpClient
import kotlin.jvm.Volatile
import okhttp3.Request
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by Duke
 */
class OkHttpReq private constructor() {
    private val okHttpClient: OkHttpClient
    private val TIME_OUT_SECONDS: Long = 30

    operator fun get(url: String): String? {
        val builder = Request.Builder()
                .get()
                .url(url)
                .build()
        try {
            val response = okHttpClient.newCall(builder).execute()

            if (200 == response.code) {
                val content = response.body!!.bytes()

                return String(content, Charset.forName("UTF-8"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        @Volatile
        private var _instance: OkHttpReq? = null
        val instance: OkHttpReq
            get() {
                if (null == _instance) {
                    synchronized(OkHttpReq::class.java) {
                        if (null == _instance) {
                            _instance = OkHttpReq()
                        }
                    }
                }
                return _instance!!
            }
    }

    init {
        okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .build()
    }
}