package com.umbrel.android.runtime

import android.util.Log
import com.umbrel.android.core.parser.AndroidServiceConfig
import com.umbrel.android.runtime.service.ServiceInfo
import com.umbrel.android.runtime.service.ServiceLifecycleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRunner(private val lifecycleManager: ServiceLifecycleManager) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun launchApp(appId: String, services: List<AndroidServiceConfig>) {
        Log.d("AppRunner", "Launching app: ${appId} with ${services.size} services")

        for (service in services) {
            val command = service.command ?: "sh -c '/bin/start.sh'" // Default start script
            val serviceInfo = ServiceInfo(
                id = "${appId}_${service.name}",
                name = "${appId} - ${service.name}",
                command = command
            )

            lifecycleManager.startService(serviceInfo)
        }
    }

    fun stopApp(appId: String, serviceNames: List<String>) {
        for (name in serviceNames) {
            lifecycleManager.stopService("${appId}_${name}")
        }
    }
}
