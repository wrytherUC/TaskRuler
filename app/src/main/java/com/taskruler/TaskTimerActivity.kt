package com.taskruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.taskruler.view.CountDownView
import org.koin.androidx.viewmodel.ext.android.viewModel


class TaskTimerActivity : ComponentActivity() {
    private val viewModel: TimerMainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val taskTime = intent.getStringExtra("Time")!!.toLong() * 60000L

        setContent {
            CountDownView(viewModel,taskTime)
        }
    }
}