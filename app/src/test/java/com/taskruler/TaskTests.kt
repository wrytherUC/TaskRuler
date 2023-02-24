package com.taskruler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.taskruler.dto.Task
import com.taskruler.service.TaskService
import junit.framework.TestCase.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class TaskTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    lateinit var mockTaskService : TaskService
    val laundrytask = Task(0,"Do Laundry", isCompleted = false)

    var tasks : List<Task>? = ArrayList<Task>()


    //Test File
    @Test
    fun `Add a task to a Database`() {
        givenTaskDatabaseExists()
        whenDoLaundryisEntered()
        DoLaundryIsAddedToDatabase()


    }

    private fun givenTaskDatabaseExists() {
        assertNotNull(tasks)
    }

    private fun whenDoLaundryisEntered() {
        assertNotNull(laundrytask)
    }

    private fun DoLaundryIsAddedToDatabase() {
        var addTask = ArrayList<Task>()

        addTask.add(laundrytask)

        tasks = addTask

    }

    @Test
    fun `Delete task from database`() {
        givenTaskDatabaseExists()
        whenDoLaundryisEntered()
        deleteTask()

    }

    private fun deleteTask() {
        var removeTask = tasks

        if (removeTask != null) {
            removeTask.drop(0)
        }

        tasks = removeTask


    }






}


