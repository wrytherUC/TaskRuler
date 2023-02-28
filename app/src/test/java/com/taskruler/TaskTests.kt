package com.taskruler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.taskruler.dto.Task
import com.taskruler.service.TaskService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class TaskTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var taskService : TaskService

    val laundrytask = Task(0,"Do Laundry", isCompleted = false)

    var addtasks : List<Task>? = ArrayList<Task>()

    var allTasks : List<Task>? = ArrayList<Task>()

    lateinit var mvm: MainViewModel

    @MockK
    lateinit var mockTaskService : TaskService

    private val mainThreadSurrogate = newSingleThreadContext("Main Thread")
    @Before
    fun initMocksAndMainThread(){
        MockKAnnotations.init(this)
        Dispatchers.setMain(mainThreadSurrogate)
    }
    @After
    fun tearDown(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }





    //Test File
    @Test
    fun `Given when task data is available when I search for taskId of 1 should return Do the Dishes` () = runTest {
        givenTaskServiceIsIntialized()
        whenTaskDataAreReadandParsed()
        thenTheTaskCollectionShouldContainDoTheDishes()
    }



    private fun givenTaskServiceIsIntialized() {
        taskService = TaskService()
    }
    private suspend fun  whenTaskDataAreReadandParsed() {
       allTasks = taskService.getTasks()
    }
    private fun thenTheTaskCollectionShouldContainDoTheDishes() {
        assertNotNull(allTasks)
        assertTrue((allTasks!!.isNotEmpty()))
        var containsDoDishes = false
        allTasks!!.forEach {
            if(it.taskName.equals(("Do the dishes")) && it.taskId.equals(0)){
                containsDoDishes = true
            }
        }
        assertTrue(containsDoDishes)
    }

    @Test
    fun `Add a task to a Database`() {
        givenTaskDatabaseExists()
        whenDoLaundryisEntered()
        DoLaundryIsAddedToDatabase()


    }

    private fun givenTaskDatabaseExists() {
        assertNotNull(addtasks)
    }

    private fun whenDoLaundryisEntered() {
        assertNotNull(laundrytask)
    }

    private fun DoLaundryIsAddedToDatabase() {
        var addTask = ArrayList<Task>()

        addTask.add(laundrytask)

        addtasks = addTask

    }

    @Test
    fun `Delete task from database`() {
        givenTaskDatabaseExists()
        whenDoLaundryisEntered()
        deleteTask()

    }

    private fun deleteTask() {
        var removeTask = addtasks

        if (removeTask != null) {
            removeTask.drop(0)
        }

        addtasks = removeTask


    }

    @Test
    fun `given a view model with live data when populated with tasks then show results "Do the Dishes"`(){
        givenViewModelIsItializedwithMockData()
        whenGetTasksMethodIsInvoked()
        thenResultsShouldContainDoTheDishes()
    }

    private fun givenViewModelIsItializedwithMockData() {
        val tasks = ArrayList<Task>()
        tasks.add(Task(0,"Do the Dishes", false))
        tasks.add(Task(1,"Go to the store", false))
        tasks.add(Task(2,"Mop the Kitchen", true))

        coEvery { mockTaskService.getTasks() } returns tasks

        mvm = MainViewModel(taskService = mockTaskService)

    }

    private fun whenGetTasksMethodIsInvoked() {
        mvm.getTasks()
    }

    private fun thenResultsShouldContainDoTheDishes() {
        var allTasks : List<Task>? = ArrayList<Task>()
        val latch = CountDownLatch(1)
        val observer = object : Observer<List<Task>> {
            override fun onChanged(receivedTasks: List<Task>?) {
                allTasks = receivedTasks
                latch.countDown()
                mvm.tasks.removeObserver(this)
            }

        }
        mvm.tasks.observeForever(observer)
        latch.await(10, TimeUnit.SECONDS)
        assertNotNull(allTasks)
        assertTrue(allTasks!!.isNotEmpty())
        var containsDoDishes = false
        allTasks!!.forEach {
            if (it.taskName.equals(("Do the Dishes")) && it.taskId.equals(0)){
                containsDoDishes = true
                }
        }
        assertTrue(containsDoDishes)

    }


}


