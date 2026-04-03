package com.umbrel.android.runtime

import android.util.Log
import com.umbrel.android.runtime.service.ServiceLifecycleManager
import com.umbrel.android.runtime.service.ServiceInfo
import kotlinx.coroutines.*

class BackgroundSupervisor(private val lifecycleManager: ServiceLifecycleManager) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val trackedServices = mutableListOf<ServiceInfo>()

    fun track(service: ServiceInfo) {
        trackedServices.add(service)
        lifecycleManager.startService(service)
    }

    fun startMonitoring() {
        scope.launch {
            while (isActive) {
                for (service in trackedServices) {
                    if (!lifecycleManager.isRunning(service.id) && service.status != "Stopped") {
                        Log.i("Supervisor", "Service ${service.id} crashed or stopped, restarting...")
                        lifecycleManager.startService(service)
                    }
                }
                delay(5000) // Check every 5 seconds
            }
        }
    }

    fun stopAll() {
        scope.cancel()
        trackedServices.forEach { lifecycleManager.stopService(it.id) }
    }
}
