package com.taskruler.service

import com.taskruler.RetrofitClientInstance
import com.taskruler.dao.IActivityDAO
import com.taskruler.dto.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

/**
 * Service that will invoke the ITaskDAO class
 */
interface IActivityService {
    /**
     * Interface for the Task Service class
     * Instantiated by Koin
     * @returns a list of Tasks
     */
    suspend fun getActivities() : List<Activity>?
}


class ActivityService : IActivityService {

    /**
     * @returns a list of Tasks that the RetroClientInstance retrieves from the online source
     * @await for the retrofitClientInstance to retrieve the data from online and turn the JSON into tasks
     */
   override suspend fun getActivities() : List<Activity>? {
    return withContext(Dispatchers.IO){
        val service = RetrofitClientInstance.retrofitInstance?.create(IActivityDAO::class.java)
        val activities = async {service?.getAllActivities()}
        var results = activities.await()?.awaitResponse()?.body()
        return@withContext results
    }
    }

}