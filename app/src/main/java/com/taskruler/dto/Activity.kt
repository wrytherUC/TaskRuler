package com.taskruler.dto

import androidx.room.Entity


/**
 * Data Transfer Object to transfer activities and activity data from Retrofit to MVM
 */
@Entity(tableName="tasks")
data class Activity (var activityId : Int = 0,
                     var activityName : String = "",
                     //Might need updated to string when implement drop down box for this choice
                     var isCompleted : Boolean = false) {
                     //var isCompleted : String = ""
    override fun toString(): String {
        return activityName
    }
}