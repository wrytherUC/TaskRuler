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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.tasks.Task
import com.taskruler.utilities.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {

    private var selectedActivity: Activity? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var inTaskName: String = ""
    private var selectedCompleted: String = ""

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

@Composable
fun UserTasksList(
    name: String,
    activities: List<Activity> = ArrayList(),
    userTasks: List<UserTask> = ArrayList(),
    selectedUserTask : UserTask = UserTask()) {

    val inTaskName by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTaskName) }
    val inIsCompleted by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userIsCompleted) }
    var inTaskTotalTime by remember(selectedUserTask.userTaskId) { mutableStateOf(selectedUserTask.userTotalTaskTime) }
    val context = LocalContext.current
    val localContext = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    var chosenYear: Int
    var chosenMonth: Int
    var chosenDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    var chosenHour: Int
    var chosenMin: Int

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )

    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }

    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        }, mHour, mMinute, false
    )

    Column {
        TaskSpinner(userTasks = userTasks)

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center

            ) {

                TextFieldWithDropdownUsage(dataIn = activities, "Activity Name", 3, selectedUserTask)

                }
            }

        OutlinedTextField(
            value = inTaskTotalTime,
            onValueChange = { inTaskTotalTime = it },
            label = { Text(stringResource(R.string.taskTotalTime)) }
        )

        TrueFalseSpinner()

        Box {
        Column {

            Button(onClick = {
                //localContext.startActivity(Intent(localContext, TaskTimerActivity::class.java))
                //https://stackoverflow.com/questions/45157567/how-to-pass-the-values-from-activity-to-another-activity

                //val intent = Intent(localContext, TaskTimerActivity::class.java)
                val intent = Intent(this@MainActivity, TaskTimerActivity::class.java)
                intent.putExtra("Time", "$inTaskTotalTime")

                startActivity(intent)
            })

            { Text(text = "Task Timer") }

            Button(
                onClick = {
                    mDatePickerDialog.show()
                },
            ) {
                Text(text = "Task Start Date", color = Color.White)
            }

            Button(
                onClick = {
                    mTimePickerDialog.show()
                },
            )
            { Text(text = "Task Start Time") }
        }
    }

        Box {
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
                        userIsCompleted = inIsCompleted
                    }
                    viewModel.saveUserTask()
                    Toast.makeText(
                        context,
                        "$inTaskName $inIsCompleted $inTaskTotalTime",
                        Toast.LENGTH_LONG
                    ).show()

                })
                {
                    Text(text = stringResource(R.string.SaveTask))
                }

                Button(onClick = {
                    val userSelectedDateTime = Calendar.getInstance()

                    chosenDay = mDate.value.split("/")[0].toInt()
                    chosenMonth = mDate.value.split("/")[1].toInt() - 1
                    chosenYear = mDate.value.split("/")[2].toInt()

                    chosenHour = mTime.value.split(":")[0].toInt()
                    chosenMin = mTime.value.split(":")[1].toInt()

                    userSelectedDateTime.set(chosenYear, chosenMonth, chosenDay, chosenHour , chosenMin)

                    val todayDateTime = Calendar.getInstance()

                    (userSelectedDateTime.timeInMillis) - (todayDateTime.timeInMillis)

                    val delayInSeconds = (userSelectedDateTime.timeInMillis/1000L) - (todayDateTime.timeInMillis/1000L)

                    createWorkRequest(inTaskName, delayInSeconds)

                    Toast.makeText(context, "Reminder set", Toast.LENGTH_SHORT).show()
                })
                {Text(text = "Create Task Notification")}
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Button(
                    onClick = {
                        signIn()
                    }

                )
                { Text(text = "Logon") }
            }
        }
    }
}

    @Composable
    fun TrueFalseSpinner(){
        val trueFalseOptions = listOf(
            getString(R.string.False),
            getString(R.string.True)
        )
        var trueFalseText by remember { mutableStateOf(getString(R.string.completedStatus))}
        var expanded by remember { mutableStateOf(false)}

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        )
        {
            Row(
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 25.dp)
                    .clickable { expanded = !expanded }
                    .border(BorderStroke(1.dp, Color.Black)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = trueFalseText)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    stringResource(androidx.compose.ui.R.string.dropdown_menu)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    trueFalseOptions.forEach { trueFalseOption ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            trueFalseText = trueFalseOption
                        }){
                            Text(text = trueFalseOption)
                            selectedCompleted = trueFalseOption
                        }

                    }

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

    /**
     * Auto Complete
     */
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

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long  ) {
        val myWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "title" to "Reminder",
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