package com.qfleng.um.util

import okhttp3.MediaType.Companion.toMediaType
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


    val JSON = "application/json; charset=utf-8".toMediaType()

    fun executeRequest(req: Request): String? {
        try {
            val response = okHttpClient.newCall(req).execute()

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
        fun getChromeRequestBuilder(): Request.Builder {
            return Request.Builder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36 Edg/95.0.1020.44")
        }


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