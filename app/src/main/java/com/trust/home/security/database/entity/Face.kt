package com.trust.home.security.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "Face_ID")
data class Face(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "user_id")
    var userId: Long? = null,

    @ColumnInfo(name = "user_name")
    var userName: String? = null,

    @ColumnInfo(name = "face_data")
    var faceData: String? = null
) {
//    fun getFaceData(): Array<Array<Float>> {
//        return Gson().fromJson(faceData, Array<Array<Float>>::class.java)
//    }
}