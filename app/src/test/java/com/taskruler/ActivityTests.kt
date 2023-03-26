package com.taskruler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.taskruler.dto.Activity
import com.taskruler.service.ActivityService
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


class ActivityTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var taskService : ActivityService

    val laundrytask = Activity(0,"Do Laundry", isCompleted = false)

    var addtasks : List<Activity>? = ArrayList<Activity>()

    var allActivities : List<Activity>? = ArrayList<Activity>()

    lateinit var mvm: MainViewModel

    @MockK
    lateinit var mockTaskService : ActivityService

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
        taskService = ActivityService()
    }
    private suspend fun  whenTaskDataAreReadandParsed() {
       allActivities = taskService.getActivities()
    }
    private fun thenTheTaskCollectionShouldContainDoTheDishes() {
        assertNotNull(allActivities)
        assertTrue((allActivities!!.isNotEmpty()))
        var containsDoDishes = false
        allActivities!!.forEach {
            if(it.activityName.equals(("Do the dishes")) && it.activityId.equals(0)){
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
        var addActivity = ArrayList<Activity>()

        addActivity.add(laundrytask)

        addtasks = addActivity

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
        var activities = ArrayList<Activity>()
        var i = 1+1
        activities.add(Activity(0,"Do the Dishes", false))
        activities.add(Activity(1,"Go to the store", false))
        activities.add(Activity(2,"Mop the Kitchen", true))

        coEvery { mockTaskService.getActivities() } returns activities

        mvm = MainViewModel(activityService = mockTaskService)

    }

    private fun whenGetTasksMethodIsInvoked() {
        mvm.getActivities()
    }

    private fun thenResultsShouldContainDoTheDishes() {
        var allActivities : List<Activity>? = ArrayList<Activity>()
        val latch = CountDownLatch(1)
        val observer = object : Observer<List<Activity>> {
            override fun onChanged(receivedActivities: List<Activity>?) {
                allActivities = receivedActivities
                latch.countDown()
                mvm.activities.removeObserver(this)
            }

        }
        mvm.activities.observeForever(observer)
        latch.await(10, TimeUnit.SECONDS)
        assertNotNull(allActivities)
        assertTrue(allActivities!!.isNotEmpty())
        var containsDoDishes = false
        allActivities!!.forEach {
            if (it.activityName.equals(("Do the Dishes")) && it.activityId.equals(0)){
                containsDoDishes = true
                }
        }
        assertTrue(containsDoDishes)

    }



}


