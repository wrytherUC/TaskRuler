package com.taskruler.dto

import androidx.room.Entity

@Entity(tableName="timers")
data class Timer (var timerId : Int, var taskId : Int, var secondsRemaining : Int) {

}

//Example
//@Entity(tableName="plants")
//data class Plant(@SerializedName("genus") var genus : String, var species : String, var common : String, var cultivar:String ="", @PrimaryKey var id : Int = 0 ) {
//    override fun toString(): String {
//        return common
//    }
//}