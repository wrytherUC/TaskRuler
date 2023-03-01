package com.taskruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.taskruler.ui.theme.TaskRulerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getTasks()
            val tasks by viewModel.tasks.observeAsState(initial = emptyList())
            TaskRulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
@Composable
fun Greeting(name: String) {

    var activityName by remember { mutableStateOf("") }
    var activityName2 by remember { mutableStateOf("") }
    var activityName3 by remember { mutableStateOf("") }
    var futureActivity by remember { mutableStateOf("") }

    Column {


        Button(onClick = { /*TODO*/ })
        {Text(text = "Home")}

        Button(onClick = { /*TODO*/ })
        {Text(text = "Task Timed")}


        TextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text(stringResource(R.string.activtyName)) }
        )


        TextField(
            value = activityName2,
            onValueChange = { activityName2 = it },
            label = { Text(stringResource(R.string.activityName2)) }
        )

        TextField(
            value = activityName3,
            onValueChange = { activityName3 = it },
            label = { Text(stringResource(R.string.activityName3)) }
        )

        TextField(
            value = futureActivity,
            onValueChange = { futureActivity = it },
            label = { Text(stringResource(R.string.futureActivity)) }
        )

        Button(onClick = { /*TODO*/ })
        {Text(text = "Start Timer")}


    }
    }


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskRulerTheme {
        Greeting("Android")
    }
}}