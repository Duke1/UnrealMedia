package com.qfleng.um.database

import android.content.Context
import androidx.room.Room

/**
 * Created by Duke
 */
class AppDbHelper private constructor(ctx: Context) {

    val appDataBase = Room
            .databaseBuilder(ctx, AppDb::class.java, DbConst.APP_DB_NAME)
            .build()


    companion object {

        @Volatile
        private var instance: AppDbHelper? = null

        fun getInstance(context: Context) = instance ?: synchronized(AppDbHelper::class) {
            instance ?: AppDbHelper(context.applicationContext).also { instance = it }
        }

    }
}