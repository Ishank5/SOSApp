package com.example.sosapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sosapp.SOSWorker
import java.util.concurrent.TimeUnit

@Composable
fun ScheduleSOSWorker() {
    LaunchedEffect(Unit) {
        val workRequest = PeriodicWorkRequestBuilder<SOSWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "SOSWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}