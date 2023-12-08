package com.trust.home.security.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trust.home.security.database.entity.Face
import com.trust.home.security.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface DAOJob {
    @Insert
    fun insertFace(face: Face)

    @Query("SELECT * FROM Face_ID WHERE user_name = :userName")
    fun selectFaceWhere(userName: String): Face?

    @Insert
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM User_Data WHERE user_name = :userName")
    fun selectUserWhere(userName: String): User?

    @Query("SELECT * FROM User_Data WHERE user_name = :userName")
    fun selectFlowUserWhere(userName: String): Flow<User>

    @Query("SELECT * FROM User_Data WHERE user_name = :userName AND password = :password")
    fun selectUserWhere(userName: String, password: String): User?
}