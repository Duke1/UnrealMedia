package com.qfleng.um.database.entity

import androidx.room.*
import com.qfleng.um.database.DbConst

/**
 * Created by Duke
 */
@Entity(tableName = DbConst.STRING_KEY_DATA_TABLE_NAME)
class StringKeyData {
    companion object {
        @Ignore
        const val KEY_LAST_PLAY_AUDIO_URL = "LAST_PLAY_AUDIO_URL"
    }

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "key")
    var key: String = ""


    @ColumnInfo(name = "data")
    var data: String = ""

}