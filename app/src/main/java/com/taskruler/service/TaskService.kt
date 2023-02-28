package com.taskruler.service

import com.taskruler.RetrofitClientInstance
import com.taskruler.dao.ITaskDAO
import com.taskruler.dto.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class TaskService {
   suspend fun getTasks() : List<Task>? {
    return withContext(Dispatchers.IO){
        val service = RetrofitClientInstance.retrofitInstance?.create(ITaskDAO::class.java)
        val tasks = async {service?.getAllTasks()}
        var results = tasks.await()?.awaitResponse()?.body()
        return@withContext results
    }
    }

}