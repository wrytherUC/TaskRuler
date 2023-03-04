package com.taskruler.dao

import com.taskruler.dto.Task
import retrofit2.Call
import retrofit2.http.GET

/**
 * Data Access Object to encapsulate retrieving tasks from DTO/Retrofit to the Main View Model
 */
interface ITaskDAO {

    /**
     * @returns a list of tasks from the JSON source from online
     */
    @GET("/")
    fun getAllTasks() : Call<ArrayList<Task>>
}