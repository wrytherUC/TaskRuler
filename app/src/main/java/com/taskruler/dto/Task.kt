package com.taskruler.dto

import androidx.room.Entity

@Entity(tableName="tasks")
data class Task (var taskId : Int, var taskName : String, var isCompleted : Boolean) {
}

//Example
//@Entity(tableName="plants")
//data class Plant(@SerializedName("genus") var genus : String, var species : String, var common : String, var cultivar:String ="", @PrimaryKey var id : Int = 0 ) {
//    override fun toString(): String {
//        return common
//    }
//}