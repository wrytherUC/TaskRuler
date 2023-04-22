package com.taskruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.taskruler.view.CountDownView
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Class is for the task timer feature of the Task Ruler application
 * This had to be set up a separate composable activity from Task Ruler
 * An intent is set in the MainActivity for this class and will be passed
 *  the user's total task time
 * @CountDownView is what will be triggered by the intent
 */
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