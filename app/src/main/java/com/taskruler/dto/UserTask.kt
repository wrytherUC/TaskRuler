package com.taskruler.dto

import androidx.room.Entity

@Entity(tableName="UserTasks")
data class UserTask (var activityId: Int = 0,
                     var activityName: String = "",
                     var userTaskId : String = "",
                     var userTaskName : String = "",
                     var userIsCompleted : String = "",
                     var userTotalTaskTime : Int = 0) {
    override fun toString(): String {
        return "$activityName"
    }
}

//Example
//@Entity(tableName="plants")
//data class Plant(@SerializedName("genus") var genus : String, var species : String, var common : String, var cultivar:String ="", @PrimaryKey var id : Int = 0 ) {
//    override fun toString(): String {
//        return common
//    }
//}