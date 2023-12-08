package com.trust.home.security.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.trust.home.security.database.entity.Face
import com.trust.home.security.database.entity.User
import com.trust.home.security.database.local.HomeSecurityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DatabaseJobViewModel(application: Application) : AndroidViewModel(application) {
    private val parentJob by lazy { Job() }
    private val coroutineContext by lazy { parentJob + Dispatchers.Main }
    private val scope by lazy { CoroutineScope(coroutineContext) }
    private val repository: DatabaseJobRepository

    init {
        val daoJob = HomeSecurityDatabase.getDatabase(application).daoJob()
        repository = DatabaseJobRepository(daoJob)
    }

    fun insertFace(face: Face) = launchBackground {
        repository.insertFace(face)
    }

    fun selectFaceWhere(userName: String, doOnSuccess: (Face?) -> Unit) = launchBackground {
        repository.selectFaceWhere(userName).launchMainThread(doOnSuccess)
    }

    fun insertUser(user: User) = launchBackground {
        repository.insertUser(user)
    }

    fun updateUser(user: User) = launchBackground {
        repository.updateUser(user)
    }

    fun selectUserWhere(userName: String?, doOnSuccess: (User?) -> Unit) = launchBackground {
        repository.selectUserWhere(userName ?: "").launchMainThread(doOnSuccess)
    }

    fun selectFlowUserWhere(userName: String?) = repository.selectFlowUserWhere(userName ?: "")

    fun selectUserWhere(userName: String, password: String, doOnSuccess: (User?) -> Unit) = launchBackground {
        repository.selectUserWhere(userName, password).launchMainThread(doOnSuccess)
    }

    fun checkUserIsRegistered(userName: String, isRegistered: (Boolean) -> Unit) = launchBackground {
        val items = repository.selectUserWhere(userName)
        launchMainThread {
            isRegistered.invoke(items != null)
        }
    }

    fun checkUserIsRegisteredFace(userName: String, isRegistered: (Boolean) -> Unit) = launchBackground {
        val items = repository.selectFaceWhere(userName)
        launchMainThread {
            isRegistered.invoke(items != null)
        }
    }

    private fun launchBackground(doWork: suspend CoroutineScope.() -> Unit) {
        scope.launch(Dispatchers.IO) {
            doWork.invoke(this)
        }
    }

    private fun<T> T.launchMainThread(doWork: T.() -> Unit) {
        scope.launch(Dispatchers.Main) {
            doWork.invoke(this@launchMainThread)
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}