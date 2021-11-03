package com.qfleng.um.database.dao

import androidx.room.*
import com.qfleng.um.database.DbConst
import com.qfleng.um.database.entity.MusicInfo

/**
 * Created by Duke
 */

@Dao
interface MusicInfoDao {
    @Query("SELECT * FROM ${DbConst.MUSIC_INFO_TABLE_NAME}")
    fun loadAll(): List<MusicInfo>

    @Query("SELECT * FROM ${DbConst.MUSIC_INFO_TABLE_NAME} WHERE _id == :id")
    fun loadById(id: Int): MusicInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: MusicInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: ArrayList<MusicInfo>)

    @Delete
    fun delete(user: MusicInfo)

    @Query("DELETE FROM ${DbConst.MUSIC_INFO_TABLE_NAME}")
    fun deleteAll()
}