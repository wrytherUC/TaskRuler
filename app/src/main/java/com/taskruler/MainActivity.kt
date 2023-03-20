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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.taskruler.dto.Task
import com.taskruler.ui.theme.TaskRulerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private var selectedTask: Task? = null
    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getTasks()
            val tasks by viewModel.tasks.observeAsState(initial = emptyList())
            val spinnerTasks by viewModel.spinnerTasks.observeAsState(initial = emptyList())


            TaskRulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    LogActivity("Android",spinnerTasks)
                }
            }
        }
    }
@Composable
fun LogActivity(name: String, tasks: List<Task> = ArrayList<Task>()) {

    var activityName by remember { mutableStateOf("") }
    var activityName2 by remember { mutableStateOf("") }
    var activityName3 by remember { mutableStateOf("") }
    var futureActivity by remember { mutableStateOf("") }

    Column {
        TaskSpinner(tasks = tasks)


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

        Button(onClick = {
            signIn()
        })
        {Text(text = "Logon")}


    }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers).build()
    }

    @Composable
    fun TaskSpinner (tasks: List<Task>){
        var taskText by remember { mutableStateOf("Task List")}
        var expanded by remember { mutableStateOf(false)}
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
            Row(Modifier
                .padding(24.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = taskText, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    tasks.forEach(){
                        task -> DropdownMenuItem(onClick = {
                            expanded = false
                        taskText = task.toString()
                        selectedTask = task
                    }) {
                            Text(text = task.toString())

                    }
                    }

                }
            }
        }

    }



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskRulerTheme {
        LogActivity("Android")
    }
}}