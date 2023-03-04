package com.taskruler

import com.taskruler.service.ITaskService
import com.taskruler.service.TaskService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * AppModule can declare any classes that we want Koin to manage object instantiation for
 */
@JvmField
val appModule = module {
    viewModel { MainViewModel(get()) }
    single<ITaskService> { TaskService() }
}