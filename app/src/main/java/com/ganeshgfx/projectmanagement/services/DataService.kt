package com.ganeshgfx.projectmanagement.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Notice
import com.ganeshgfx.projectmanagement.repositories.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class DataService : JobService() {
    private var jobCanceled = false
    //private val job = SupervisorJob()
    //private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var repo: MainRepository

    @Inject
    lateinit var notifications: NotificationHelper

    @Inject
    lateinit var scope: CoroutineScope

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        //scope.launch {
            doBackgroundWork(jobParameters)
            //jobFinished(jobParameters, false)
        //}
//        scope.launch {
//            var time = 0
//            val str = randomString(10)
//            while (true) {
//                delay(1000)
//                log("JOB RUNNING", str, time)
//                time++
//            }
//        }
        return true
    }

    private fun doBackgroundWork(jobParameters: JobParameters): Boolean {
       // notifications.show(Notice("Project Management", "JOB Started"))
        repo.notification.onEach {
            //log(it)
            notifications.show(it)
        }.launchIn(scope)
        repo.startJob()
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        //notifications.show(Notice("Project Management", "JOB Stoped"))
        jobCanceled = true
        return false
    }
}