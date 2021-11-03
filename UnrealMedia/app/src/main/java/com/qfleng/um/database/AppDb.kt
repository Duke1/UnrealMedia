package com.qfleng.um.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qfleng.um.database.dao.MusicInfoDao
import com.qfleng.um.database.dao.StringKeyDataDao
import com.qfleng.um.database.entity.MusicInfo
import com.qfleng.um.database.entity.StringKeyData

/**
 * Created by Duke
 */
@Database(entities = [MusicInfo::class, StringKeyData::class], version = 1, exportSchema = true)
abstract class AppDb : RoomDatabase() {
    abstract fun musicInfoDao(): MusicInfoDao
    abstract fun stringKeyDataDao(): StringKeyDataDao
}
