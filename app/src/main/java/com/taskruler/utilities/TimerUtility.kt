package com.taskruler.utilities

import java.util.concurrent.TimeUnit

object TimerUtility {
    //time to countdown - 1min, this val is what is determining the countdown
    private const val TIME_FORMAT = "%02d:%02d"

    //convert time to milli seconds
    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}