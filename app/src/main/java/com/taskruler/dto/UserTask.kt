package com.taskruler.dto

import androidx.room.Entity

/**
 * Data Transfer Object to create from user input
 */
@Entity(tableName="UserTasks")
data class UserTask (var activityId: Int = 0,
                     var activityName: String = "",
                     var userTaskId : String = "",
                     var userTaskName : String = "",
                     var userProgress : String = "",
                     var userTotalTaskTime : String = "") {
    override fun toString(): String {
        return "Activity: $activityName "
    }
}