package com.taskruler.dto

import androidx.room.Entity

@Entity(tableName="UserTasks")
data class UserTask (var userTaskID : Int, var userTaskName : String, var totalUserTaskTime : Int) {

}

//Example
//@Entity(tableName="plants")
//data class Plant(@SerializedName("genus") var genus : String, var species : String, var common : String, var cultivar:String ="", @PrimaryKey var id : Int = 0 ) {
//    override fun toString(): String {
//        return common
//    }
//}