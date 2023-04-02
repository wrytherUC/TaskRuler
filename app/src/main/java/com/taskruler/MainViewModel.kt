package com.taskruler

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.taskruler.dto.Activity
import com.taskruler.dto.User
import com.taskruler.dto.UserTask
import com.taskruler.service.IActivityService
import com.taskruler.service.ActivityService
import kotlinx.coroutines.launch

/**
 * Represents Jetpack Compose liveData
 * Organizes DTO/DAO
 * @property activityService is using the TaskService class
 * @property MutableLiveData<List<Task>> watching changing list of tasks
 * @property user being set from MainActivity signInResult
 */
class MainViewModel(var activityService : IActivityService = ActivityService()) : ViewModel() {

    var activities : MutableLiveData<List<Activity>> = MutableLiveData<List<Activity>>()
    var userTasks : MutableLiveData<List<UserTask>> = MutableLiveData<List<UserTask>>()
    var selectedUserTask by mutableStateOf(UserTask())
    var user : User? = null
    internal val NEW_TASK: String = "New Task"

    private var firestore : FirebaseFirestore

    /**
     * TODO
     *      This init block is causing an issue with MVM unit test. Need to resolve
     */
    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    /**
     * Function listens for any changes to any changes with user's Firebase DB task documents
     * Called by main activity
     */
    fun listenToUserTasks(){
        user?.let {
            user ->
            firestore.collection("users").document(user.uid).collection("tasks").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Listen failed", e)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val allUserTasks = ArrayList<UserTask>()
                    allUserTasks.add(UserTask(activityName = NEW_TASK))
                    val documents = snapshot.documents
                    documents.forEach {
                        val userTask = it.toObject(UserTask::class.java)
                        userTask?.let {
                            allUserTasks.add(it)
                        }
                    }
                    userTasks.value = allUserTasks

                }
            }
        }
    }

    /**
     * @return activities from ActivityService
     * Adds/posts tasks to the MutableLiveData<List<Activities>>
     */
    fun getActivities() {
        viewModelScope.launch {
            val innerActivities = activityService.getActivities()
            activities.postValue(innerActivities)
        }
    }

    /**
     * Function will save user input to a Firebase DB task document
     * If new, will create a new document
     * If not new, will update the document using existing userTaskId
     * Called by main activity
     */
    fun saveUserTask() {
        user?.let {
            user ->
            val document =
            if (selectedUserTask.userTaskId.isEmpty()) {
                //creating a new task document for specific user
                firestore.collection("users").document(user.uid).collection("tasks").document()
            }
            else {
                //updating an existing task document for specific user
                firestore.collection("users").document(user.uid).collection("tasks").document(selectedUserTask.userTaskId)
            }

            selectedUserTask.userTaskId = document.id
            val handle = document.set(selectedUserTask)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Document save failure $it") }
        }
    }

    /**
     * function is called in MainActivity from signInResult function
     *      part of the signIn intent functionality
     */
    fun saveUser() {
        user?.let {
            user ->
            val handle = firestore.collection("users").document(user.uid).set(user)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Document save failure $it") }
        }
    }
}