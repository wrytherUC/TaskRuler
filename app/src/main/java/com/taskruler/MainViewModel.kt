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

    private lateinit var firestore : FirebaseFirestore


    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    fun listenToUserTasks(){
        user?.let {
            user ->
            firestore.collection("users").document(user.uid).collection("tasks").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Listen failed", e)
                    return@addSnapshotListener
                }
                //Need to compare the below snapshot let block against class code
                    //allSpecimens.add(Specimen(plantName = NEW_SPECIMEN))
                snapshot?.let {
                    val allUserTasks = ArrayList<UserTask>()
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
            var innerActivities = activityService.getActivities()
            activities.postValue(innerActivities)
        }
    }

    /**
     *
     */
    //Want to change this from activity stuff to user tasks
    //Might need to double check this against module 7
    fun saveUserTask() {
        user?.let {
            user ->
            val document =
            if (selectedUserTask.userTaskId == null || selectedUserTask.userTaskId.isEmpty()) {
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
            val handle = firestore.collection("users").document(user!!.uid).set(user!!)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Document save failure $it") }
        }
    }
}