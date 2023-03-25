package com.taskruler

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.taskruler.dto.Task
import com.taskruler.dto.User
import com.taskruler.service.ITaskService
import com.taskruler.service.TaskService
import kotlinx.coroutines.launch

/**
 * Represents Jetpack Compose liveData
 * Organizes DTO/DAO
 * @property taskService is using the TaskService class
 * @property MutableLiveData<List<Task>> watching changing list of tasks
 * @property user being set from MainActivity signInResult
 */
class MainViewModel(var taskService : ITaskService = TaskService()) : ViewModel() {

    var tasks : MutableLiveData<List<Task>> = MutableLiveData<List<Task>>()
    var spinnerTasks : MutableLiveData<List<Task>> = MutableLiveData<List<Task>>()
    var user : User? = null

    private lateinit var firestore : FirebaseFirestore


    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    fun listenToTasks(){
        user?.let {
            user ->
            firestore.collection("users").document(user.uid).collection("tasks").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Listen failed", e)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val allTasks = ArrayList<Task>()
                    val documents = snapshot.documents
                    documents.forEach {
                        val task = it.toObject(Task::class.java)
                        task?.let {
                            allTasks.add(it)
                        }
                    }
                    spinnerTasks.value = allTasks

                }
            }
        }
    }



    /**
     * @return tasks from TaskService
     * Adds/posts tasks to the MutableLiveData<List<Task>>
     */
    fun getTasks() {
        viewModelScope.launch {
            var innerTasks = taskService.getTasks()
            tasks.postValue(innerTasks)
        }
    }

    /**
     *
     */
    fun saveTask() {
        user?.let {
            val docuement =
                if (selectedTask.taskId == null || selectedTask.taskId.isEmpty()) {
                    //creating a new task document for specific user
                    firestore.collection("users").document(user.uid).collection("tasks").document()
                }
                else {
                    //updating an existing task document for specific user
                    firestore.collection("users").document(user.uid).collection("tasks").document(selectedTask.taskId)
                }
            selectedTask.taskID = document.id
            val handle = document.set(selectedTask)
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