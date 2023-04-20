package com.taskruler.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskruler.R
import com.taskruler.utilities.TimerUtility
import com.taskruler.utilities.TimerUtility.formatTime
import com.taskruler.view.components.CountDownButton
import com.taskruler.view.components.CountDownIndicator
import com.taskruler.TimerMainViewModel
import com.taskruler.utilities.TimerUtility.TIME_COUNTDOWN

@Composable
fun CountDownView(viewModel: TimerMainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), taskTime: Long) {

    TIME_COUNTDOWN = taskTime
    val taskTimeDisplay = TIME_COUNTDOWN.formatTime()
    val time by viewModel.time.observeAsState(TIME_COUNTDOWN.formatTime())
    val progress by viewModel.progress.observeAsState(1.00F)
    val isPlaying by viewModel.isPlaying.observeAsState(false)

    CountDownView(time = time, progress = progress, isPlaying = isPlaying, taskTime = taskTimeDisplay) {
        viewModel.handleCountDownTimer()
    }

}

@Composable
fun CountDownView(
    time: String,
    progress: Float,
    isPlaying: Boolean,
    taskTime: String,
    optionSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Timer",
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            fontFamily = FontFamily(Font(R.font.poppins_semibold))

        )

        Text(
            text = "$taskTime minute(s) for task",
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            fontFamily = FontFamily(Font(R.font.poppins_semibold))
        )

        Text(
            text = "Click to start or stop countdown",
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            fontFamily = FontFamily(Font(R.font.poppins_regular))
        )

        CountDownIndicator(
            Modifier.padding(top = 50.dp),
            progress = progress,
            time = time,
            size = 250,
            stroke = 12
        )

        CountDownButton(

            modifier = Modifier
                .padding(top = 70.dp)
                .size(70.dp),
            isPlaying = isPlaying
        ) {
            optionSelected()
        }


    }

}