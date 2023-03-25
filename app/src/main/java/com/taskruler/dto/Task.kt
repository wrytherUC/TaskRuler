package com.taskruler.dto

import androidx.room.Entity


/**
 * Data Transfer Object to transfer tasks and task data from Retrofit to MVM
 */
@Entity(tableName="tasks")
data class Task (var taskId : Int =0,
                 var taskName : String = "",
                 var isCompleted : Boolean = false,
                 var startDate: String = "") {
}

//Example
//@Entity(tableName="plants")
//data class Plant(@SerializedName("genus") var genus : String, var species : String, var common : String, var cultivar:String ="", @PrimaryKey var id : Int = 0 ) {
//    override fun toString(): String {
//        return common
//    }
//}