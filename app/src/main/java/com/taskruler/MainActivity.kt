package com.taskruler

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.taskruler.utilities.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private var selectedActivity: Activity? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val viewModel: MainViewModel by viewModel()
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

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.notificationsAvailable), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.notificationsUnavailable), Toast.LENGTH_LONG).show()
        }
    }

@Composable
fun UserTasksList(
    name: String,
    activities: List<Activity> = ArrayList(),
    userTasks: List<UserTask> = ArrayList(),
    selectedUserTask : UserTask = UserTask()) {

    var inTaskName by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTaskName) }
    var inTaskProgress by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userProgress) }
    var inTaskTotalTime by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTotalTaskTime) }
    val context = LocalContext.current

    var isEnabled by remember { mutableStateOf(false)}

    var chosenYear: Int
    var chosenMonth: Int
    var chosenDay: Int
    var chosenHour: Int
    var chosenMin: Int
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDate = remember { mutableStateOf("") }

    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    val mTime = remember { mutableStateOf("") }

    val mDateAndTimePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDayOfMonth ->
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, mHour, mMinute ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            mTime.value = "$mHour:$mMinute"
        }, mHour, mMinute, false).show()
    }, mYear, mMonth, mDay)


    Column {
        TaskSpinner(userTasks = userTasks)

        Box(
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center

            ) {

                TextFieldWithDropdownUsage(dataIn = activities, stringResource(R.string.activity_name), 3, selectedUserTask)

            }
        }

        OutlinedTextField(
            value = inTaskTotalTime,
            onValueChange = { inTaskTotalTime = it },
            label = { Text(stringResource(R.string.taskTotalTime)) }
        )
       
        OutlinedTextField(
            value = inTaskProgress,
            onValueChange = {inTaskProgress = it},
            label = {Text(stringResource(R.string.taskProgressDescription))})
               
        Box(
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Button(onClick = {
                    selectedUserTask.apply {
                        activityName = inActivityName
                        activityId = selectedActivity?.let {
                            it.activityId
                        } ?: 0
                        userTaskName = inTaskName
                        userTotalTaskTime = inTaskTotalTime
                        userProgress = inTaskProgress
                    }
                    viewModel.saveUserTask()
                    Toast.makeText(
                        context,
                        "$inActivityName  in  $inTaskTotalTime",
                        Toast.LENGTH_LONG
                    ).show()

                })
                {
                    Text(text = stringResource(R.string.SaveTask))
                }
            }
        }

        Box(
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Button(onClick = {                 
                val intent = Intent(this@MainActivity, TaskTimerActivity::class.java)
                intent.putExtra("Time", "$inTaskTotalTime")

                startActivity(intent)
                })

                { Text(text = stringResource(R.string.taskTimer)) }
            }
        }

        Box()
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center

            )
            {

                Button(
                    modifier = Modifier
                        .padding(end = 10.dp),
                    onClick = {
                        mDateAndTimePicker.show()
                        if (mDate.value != null) {
                            isEnabled = true
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.openDataPicker), color = Color.White)
                }
            }
        }

        Box() {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center

            ) {
                Button(enabled = isEnabled,
                        onClick = {
                    val userSelectedDateTime = Calendar.getInstance()

                    chosenDay = mDate.value.split("/")[0].toInt()
                    chosenMonth = mDate.value.split("/")[1].toInt() - 1
                    chosenYear = mDate.value.split("/")[2].toInt()

                    chosenHour = mTime.value.split(":")[0].toInt()
                    chosenMin = mTime.value.split(":")[1].toInt()

                    userSelectedDateTime.set(chosenYear, chosenMonth, chosenDay, chosenHour , chosenMin)

                    val todayDateTime = Calendar.getInstance()

                    (userSelectedDateTime.timeInMillis) - (todayDateTime.timeInMillis)

                    var delayInSeconds =
                        (userSelectedDateTime.timeInMillis / 1000L) - (todayDateTime.timeInMillis / 1000L)

                    createWorkRequest(inTaskName, inTaskName, delayInSeconds)

                    Toast.makeText(context, "Reminder set", Toast.LENGTH_SHORT).show()

                })
                { Text(text = stringResource(R.string.createTaskNotification)) }
            }
        }

        Box(

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center

            ) {
                Button(
                    onClick = {
                        signIn()
                    }
                )
                { Text(text = "Log in") }
            }
        }
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
                    userTasks.forEach{
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
            inActivityName = value.text
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

    /**
     * Sign in function for user authentication
     */
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

    fun hasNotificationPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

    private fun createWorkRequest(message: String, title: String, timeDelayInSeconds: Long  ) {
        val myWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "title" to title,
                "message" to message,
            )
            )
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
    }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskRulerTheme {
        UserTasksList("Android")
    }
}}
