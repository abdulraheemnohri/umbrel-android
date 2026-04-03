package com.umbrel.android.runtime.service

import android.util.Log
import com.umbrel.android.runtime.ProotRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

data class ServiceInfo(
    val id: String,
    val name: String,
    val command: String,
    var status: String = "Stopped",
    var pid: Int? = null
)

class ServiceLifecycleManager(private val prootRunner: ProotRunner) {
    private val activeServices = ConcurrentHashMap<String, Process>()
    private val serviceJobs = ConcurrentHashMap<String, Job>()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startService(service: ServiceInfo) {
        if (activeServices.containsKey(service.id)) {
            Log.w("ServiceManager", "Service ${service.id} is already running")
            return
        }

        val job = scope.launch {
            try {
                Log.d("ServiceManager", "Starting service: ${service.name}")
                val process = prootRunner.execute(service.command)
                activeServices[service.id] = process
                service.status = "Running"

                // Monitor process completion
                process.waitFor()
                Log.d("ServiceManager", "Service ${service.id} stopped with exit code ${process.exitValue()}")
            } catch (e: Exception) {
                Log.e("ServiceManager", "Error running service ${service.id}", e)
                service.status = "Error"
            } finally {
                activeServices.remove(service.id)
                serviceJobs.remove(service.id)
            }
        }
        serviceJobs[service.id] = job
    }

    fun stopService(serviceId: String) {
        serviceJobs[serviceId]?.cancel()
        activeServices[serviceId]?.destroy()
        activeServices.remove(serviceId)
        Log.d("ServiceManager", "Service ${serviceId} stop requested")
    }

    fun restartService(service: ServiceInfo) {
        stopService(service.id)
        startService(service)
    }

    fun isRunning(serviceId: String): Boolean = activeServices.containsKey(serviceId)
}
