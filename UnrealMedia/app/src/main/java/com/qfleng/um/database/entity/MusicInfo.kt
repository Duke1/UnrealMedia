package com.qfleng.um.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.qfleng.um.database.DbConst

/**
 * Created by Duke
 */
@Entity(tableName = DbConst.MUSIC_INFO_TABLE_NAME,
        indices = [Index(value = ["path"], unique = true)]
)
class MusicInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "path")
    var path: String = ""

    @ColumnInfo(name = "data")
    var data: String = ""

}