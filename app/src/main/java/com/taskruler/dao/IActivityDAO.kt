package com.taskruler.dao

import com.taskruler.dto.Activity
import retrofit2.Call
import retrofit2.http.GET

/**
 * Data Access Object to encapsulate retrieving tasks from DTO/Retrofit to the Main View Model
 */
interface IActivityDAO {

    /**
     * @returns a list of activities from the JSON source from online
     */
    @GET("/")
    fun getAllActivities() : Call<ArrayList<Activity>>
}