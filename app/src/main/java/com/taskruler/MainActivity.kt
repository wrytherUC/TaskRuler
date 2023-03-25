package com.taskruler

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.taskruler.dto.Task
import com.taskruler.ui.theme.TaskRulerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private var selectedTask by mutableStateOf(Task())
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var inTaskName: String = ""

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

                    LogActivity("Android", spinnerTasks, selectedTask)
                }
            }
        }
    }
@Composable
fun LogActivity(name: String, tasks: List<Task> = ArrayList<Task>(), selectedTask : Task = Task()) {

    var activityName by remember { mutableStateOf(selectedTask.taskName) }
    var activityStatus by remember { mutableStateOf(selectedTask.isCompleted.toString()) }


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
            value = activityStatus,
            onValueChange = { activityStatus = it },
            label = { Text(stringResource(R.string.completedStatus)) }
        )



        Button(onClick = { /*TODO*/ })
        {Text(text = "Save Task")}

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

        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher = registerForActivityResult (
        FirebaseAuthUIActivityResultContract()
    ) {
            res -> this.signInResult(res)
    }

    private fun signInResult(result: FirebaseAuthUIAuthenticationResult) {
        val callbackResponse = result.idpResponse
        if (result.resultCode == RESULT_OK){
            firebaseUser = FirebaseAuth.getInstance().currentUser
        }
        else {
            Log.e("MainActivity.kt", "Error with logging in " + callbackResponse?.error?.errorCode)
        }
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
    //Auto Complete
    @Composable
    fun TextFieldWithDropdownUsage(dataIn: List<Task>, label : String = "", take :Int = 3, selectedTask : Task = Task()) {

        val dropDownOptions = remember { mutableStateOf(listOf<Task>()) }
        val textFieldValue = remember(selectedTask.taskId) { mutableStateOf(TextFieldValue(selectedTask.taskName)) }
        val dropDownExpanded = remember { mutableStateOf(false) }

        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        fun onValueChanged(value: TextFieldValue) {
            inTaskName = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = dataIn.filter {
                it.toString().startsWith(value.text) && it.toString() != value.text
            }.take(take)
        }

        TextFieldWithDropdown(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue.value,
            setValue = ::onValueChanged,
            onDismissRequest = ::onDropdownDismissRequest,
            dropDownExpanded = dropDownExpanded.value,
            list = dropDownOptions.value,
            label = label
        )
    }

    @Composable
    fun TextFieldWithDropdown(
        modifier: Modifier = Modifier,
        value: TextFieldValue,
        setValue: (TextFieldValue) -> Unit,
        onDismissRequest: () -> Unit,
        dropDownExpanded: Boolean,
        list: List<Task>,
        label: String = ""
    ) {
        Box(modifier) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused)
                            onDismissRequest()
                    },
                value = value,
                onValueChange = setValue,
                label = { Text(label) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = dropDownExpanded,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = onDismissRequest
            ) {
                list.forEach { text ->
                    DropdownMenuItem(onClick = {
                        setValue(
                            TextFieldValue(
                                text.toString(),
                                TextRange(text.toString().length)
                            )
                        )
                        selectedTask = text
                    }) {
                        Text(text = text.toString())
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