package com.trust.home.security.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User_Data")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "user_name")
    var userName: String? = null,

    @ColumnInfo(name = "password")
    var password: String? = null,

    @ColumnInfo(name = "avatar")
    var avatar: String? = null,

    @ColumnInfo(name = "age")
    var age: String? = null,

    @ColumnInfo(name = "gender")
    var gender: Int? = null,

    @ColumnInfo(name = "full_name")
    var fullName: String? = null
)