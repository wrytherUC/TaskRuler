package com.taskruler.dao

import com.taskruler.dto.Task
import retrofit2.Call
import retrofit2.http.GET

interface ITaskDAO {
    @GET("/")
    fun getAllTasks() : Call<ArrayList<Task>>
}