package com.taskruler.service

import com.taskruler.RetrofitClientInstance
import com.taskruler.dao.ITaskDAO
import com.taskruler.dto.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

interface ITaskService {
    /*
        @returns a list of Tasks
     */
    suspend fun getTasks() : List<Task>?
}


class TaskService : ITaskService {

    /*
        @returns a list of Tasks that the RetroClientInstance retrieves from the online source
        @await for the retrofitClientInstance to retrieve the data from online and turn the JSON into tasks
     */
   override suspend fun getTasks() : List<Task>? {
    return withContext(Dispatchers.IO){
        val service = RetrofitClientInstance.retrofitInstance?.create(ITaskDAO::class.java)
        val tasks = async {service?.getAllTasks()}
        var results = tasks.await()?.awaitResponse()?.body()
        return@withContext results
    }
    }

}