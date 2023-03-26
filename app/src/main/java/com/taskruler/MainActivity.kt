package com.taskruler

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.taskruler.dto.Activity
import com.taskruler.dto.User
import com.taskruler.dto.UserTask
import com.taskruler.ui.theme.TaskRulerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    /*
    TODO
        Switch over all task items to user tasks
        Figure out where selectedTask needs to be and selectedUserTask needs to be
            fewer selectedTasks used
     */
    private var selectedActivity: Activity? = null
    //Might not use the below selectedTask var after update
    private var selectedUserTask by mutableStateOf(UserTask())
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var inTaskName: String = ""

    private val viewModel: MainViewModel by viewModel<MainViewModel>()
    private var inActivityName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getActivities()
            firebaseUser?.let {
                val user = User(it.uid, "")
                viewModel.user = user
                viewModel.listenToUserTasks()
            }
            val activities by viewModel.activities.observeAsState(initial = emptyList())
            val userTasks by viewModel.userTasks.observeAsState(initial = emptyList())


            TaskRulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    UserTasksList("Android", activities, userTasks, viewModel.selectedUserTask)
                }
            }
        }
    }
@Composable
fun UserTasksList(
    name: String,
    activities: List<Activity> = ArrayList<Activity>(),
    userTasks: List<UserTask> = ArrayList<UserTask>(),
    selectedUserTask : UserTask = UserTask()) {

    //need to move these over from using tasks to user tasks
    //
    var inTaskName by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTaskName) }
    var inIsCompleted by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userIsCompleted) }
    var inTaskTotalTime by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTotalTaskTime) }
    val context = LocalContext.current

    Column {
        TaskSpinner(userTasks = userTasks)

        //Removing, will not have any other pages except for the main screen
        //Button(onClick = { /*TODO*/ })
        //{Text(text = "Home")}

        OutlinedTextField(
            value = inTaskName,
            onValueChange = { inTaskName = it },
            label = { Text(stringResource(R.string.activtyName)) }
        )
        //New field for user to enter in total time duration wanted for a user created task
        OutlinedTextField(
            value = inTaskTotalTime,
            onValueChange = { inTaskTotalTime = it },
            label = { Text(stringResource(R.string.taskTotalTime)) }
        )
        //Needs switched to drop down
        OutlinedTextField(
            value = inIsCompleted,
            onValueChange = { inIsCompleted = it },
            label = { Text(stringResource(R.string.completedStatus)) }
        )

        Button(onClick = {
            selectedUserTask.apply {
                activityName = inActivityName
                activityId = selectedActivity?.let {
                    it.activityId
                } ?: 0
                userTaskName = inTaskName
                userTotalTaskTime = inTaskTotalTime
                userIsCompleted = inIsCompleted
            }
            viewModel.saveUserTask()
            Toast.makeText(
                context,
                "$inTaskName $inIsCompleted $inTaskTotalTime",
                Toast.LENGTH_LONG
            ).show()
        })
        {Text(text = "Save Task")}

        Button(onClick = { /*TODO*/ })
        {Text(text = "Task Timed")}

        Button(onClick = {
            signIn()
        })
        {Text(text = "Logon")}
    }
    }
    //Missing code compared to class/PlantDiary spinner
    //Change made before PlantDiary PR #49
    @Composable
    fun TaskSpinner (userTasks: List<UserTask>){
        var userTaskText by remember { mutableStateOf("Task List")}
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
                Text(text = userTaskText, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    userTasks.forEach(){
                        userTask -> DropdownMenuItem(onClick = {
                            expanded = false
                        //Need to review this new code below
                        //See video https://youtu.be/6dZakQh1KQI
                        if (userTask.activityName == viewModel.NEW_TASK ) {
                            userTaskText = ""
                            userTask.activityName = ""
                        }
                        else {
                            userTaskText = userTask.toString()
                            selectedActivity = Activity()
                            inActivityName = userTask.activityName
                        }
                        viewModel.selectedUserTask = userTask
                    }) {
                            Text(text = userTask.toString())

                    }
                    }

                }
            }
        }

    }
    //Auto Complete
    @Composable
    fun TextFieldWithDropdownUsage(dataIn: List<Activity>, label : String = "", take :Int = 3, selectedUserTask : UserTask = UserTask()) {

        val dropDownOptions = remember { mutableStateOf(listOf<Activity>()) }
        val textFieldValue = remember(selectedUserTask.userTaskId) { mutableStateOf(TextFieldValue(selectedUserTask.activityName)) }
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
        list: List<Activity>,
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
                        selectedActivity = text
                    }) {
                        Text(text = text.toString())
                    }
                }
            }
        }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
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
            firebaseUser?.let {
                val user = User(it.uid, it.displayName)
                viewModel.user = user
                viewModel.saveUser()
                viewModel.listenToUserTasks()
            }
        }
        else {
            Log.e("MainActivity.kt", "Error with logging in " + callbackResponse?.error?.errorCode)
        }
    }


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskRulerTheme {
        UserTasksList("Android")
    }
}}