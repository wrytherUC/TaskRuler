package com.taskruler

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskruler.dto.Task
import com.taskruler.service.TaskService
import kotlinx.coroutines.launch

class MainViewModel(var taskService : TaskService = TaskService()) : ViewModel() {


    var tasks : MutableLiveData<List<Task>> = MutableLiveData<List<Task>>()


    fun getTasks() {
        viewModelScope.launch {
            var innerTasks = taskService.getTasks()
            tasks.postValue(innerTasks)
        }
    }
}