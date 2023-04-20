package com.taskruler

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskruler.helper.SingleLiveEvent
import com.taskruler.utilities.TimerUtility
import com.taskruler.utilities.TimerUtility.formatTime

class TimerMainViewModel : ViewModel() {

    //region Properties
    private var countDownTimer: CountDownTimer? = null
    //endregion

    //region States
    private val _time = MutableLiveData(TimerUtility.TIME_COUNTDOWN.formatTime())
    val time: LiveData<String> = _time

    private val _progress = MutableLiveData(1.00F)
    val progress: LiveData<Float> = _progress

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    //region Public methods
    fun handleCountDownTimer() {
        if (isPlaying.value == true) {
            pauseTimer()
        } else {
            startTimer()
        }
    }
    //endregion

    //region Private methods
    private fun pauseTimer() {
        countDownTimer?.cancel()
        handleTimerValues(false, TimerUtility.TIME_COUNTDOWN.formatTime(), 1.0F)

    }

    private fun startTimer() {

        _isPlaying.value = true
        countDownTimer = object : CountDownTimer(TimerUtility.TIME_COUNTDOWN, 1000) {

            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / TimerUtility.TIME_COUNTDOWN
                handleTimerValues(true, millisRemaining.formatTime(), progressValue)
            }

            override fun onFinish() {
                pauseTimer()
            }
        }.start()
    }

    private fun handleTimerValues(isPlaying: Boolean, text: String, progress: Float) {
        _isPlaying.value = isPlaying
        _time.value = text
        _progress.value = progress
    }
    //endregion

}