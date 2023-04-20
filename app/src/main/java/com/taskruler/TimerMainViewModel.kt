package com.taskruler

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskruler.helper.SingleLiveEvent
import com.taskruler.utilities.TimerUtility.formatTime
import com.taskruler.view.TIME_COUNTDOWN

class TimerMainViewModel : ViewModel() {

    private var _firstRun: Boolean = true
    var timeRemaining: Long = 0

    private var countDownTimer: CountDownTimer? = null
    var time : MutableLiveData<String> = MutableLiveData<String>()

    var globalProgress : MutableLiveData<Float> = MutableLiveData<Float>()

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun handleCountDownTimer() {
        if (isPlaying.value == true) {
            pauseTimer()
        } else if (_firstRun) {
            startTimer()
        }
        else {
            resumeTimer()
        }
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        handleTimerValues(false, time.value!!, globalProgress.value!!)
    }

    private fun startTimer() {
        _firstRun = false
        _isPlaying.value = true
        countDownTimer = object : CountDownTimer(TIME_COUNTDOWN, 1000) {

            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / TIME_COUNTDOWN
                timeRemaining = millisRemaining
                handleTimerValues(true, millisRemaining.formatTime(), progressValue)
            }

            override fun onFinish() {
                pauseTimer()
            }
        }.start()
    }

    private fun resumeTimer() {

        _isPlaying.value = true
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {

            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / TIME_COUNTDOWN
                timeRemaining = millisRemaining
                handleTimerValues(true, millisRemaining.formatTime(), progressValue)
            }

            override fun onFinish() {
                pauseTimer()
            }
        }.start()
    }

    private fun handleTimerValues(isPlaying: Boolean, text: String, progress: Float) {
        _isPlaying.value = isPlaying
        time.postValue(text)
        globalProgress.postValue(progress)
    }
}