package com.umbrel.android.runtime

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ResourceUsage(
    val cpuUsage: Float,
    val ramUsageMb: Long,
    val storageFreeGb: Long
)

class ResourceTracker(private val context: Context) {
    private val _usage = MutableStateFlow(ResourceUsage(0f, 0, 0))
    val usage: StateFlow<ResourceUsage> = _usage
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startTracking() {
        scope.launch {
            while (isActive) {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)

                val usedRam = (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024)
                val freeStorage = context.filesDir.freeSpace / (1024 * 1024 * 1024)

                _usage.value = ResourceUsage(
                    cpuUsage = getCpuUsage(), // Simplified placeholder
                    ramUsageMb = usedRam,
                    storageFreeGb = freeStorage
                )

                delay(2000)
            }
        }
    }

    private fun getCpuUsage(): Float {
        // Real CPU tracking would read from /proc/stat
        return (10..40).random().toFloat() / 100f
    }
}
