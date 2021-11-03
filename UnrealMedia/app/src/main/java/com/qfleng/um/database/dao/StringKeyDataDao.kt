package com.qfleng.um.database.dao

import androidx.room.*
import com.qfleng.um.database.DbConst
import com.qfleng.um.database.entity.StringKeyData

/**
 * Created by Duke
 */

@Dao
interface StringKeyDataDao {

    @Query("SELECT * FROM ${DbConst.STRING_KEY_DATA_TABLE_NAME} WHERE `key` == :key")
    fun loadByKey(key: String): StringKeyData?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(skd: StringKeyData)

    @Delete
    fun delete(user: StringKeyData)

    @Query("DELETE FROM ${DbConst.STRING_KEY_DATA_TABLE_NAME}")
    fun deleteAll()
}