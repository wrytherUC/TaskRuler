package com.taskruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskruler.dto.Task
import com.taskruler.ui.theme.TaskRulerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.w3c.dom.Text
import com.google.android.gms.tasks.Task as Task1


class MainActivity<activtyTask> : ComponentActivity() {

    private var selectedTask: Task? = null
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
    fun TaskSpinner (tasks: List<Task>){
        var activityText by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(
                Modifier
                    .padding(24.dp)
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text( text= activityText, fontSize = 18.sp, modifer = Modifier.padding(end= 8.dp))
                Icon(ImageVector = Icons.Filled.ArrowDropDown, contentDescription = "")

                DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}){
                    tasks.forEach{
                            task -> DropdownMenuItem(onClick = {
                        expanded= false
                        activityText = task.toString()
                        selectedTask = task
                    }) {
                        Text(text = task.toString())
                    }
                    }
                }
            }
        }
    }
}



    private fun Icon(ImageVector: ImageVector, contentDescription: String) {

    }

    private fun Text(text: String, fontSize: TextUnit, modifer: Modifier) {

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
}



