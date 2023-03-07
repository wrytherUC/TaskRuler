package com.taskruler

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.taskruler.dto.Task
import com.taskruler.service.ITaskService
import com.taskruler.service.TaskService
import kotlinx.coroutines.launch

/**
 * Represents Jetpack Compose liveData
 * Organizes DTO/DAO
 * @property taskService is using the TaskService class
 * @property MutableLiveData<List<Task>> watching changing list of tasks
 */
class MainViewModel(var taskService : ITaskService = TaskService()) : ViewModel() {

    var tasks : MutableLiveData<List<Task>> = MutableLiveData<List<Task>>()

    private lateinit var firestore : FirebaseFirestore

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
}