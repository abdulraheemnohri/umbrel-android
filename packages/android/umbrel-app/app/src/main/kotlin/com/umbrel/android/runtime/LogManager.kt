package com.umbrel.android.runtime

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*

class LogManager {
    private val appLogs = ConcurrentHashMap<String, MutableList<String>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun captureLogs(serviceId: String, process: Process) {
        val logs = appLogs.getOrPut(serviceId) { mutableListOf() }

        scope.launch {
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    synchronized(logs) {
                        if (logs.size > 1000) logs.removeAt(0) // Cap logs
                        logs.add(it)
                    }
                    Log.v("ServiceLog:${serviceId}", it)
                }
            }
        }
    }

    fun getLogs(serviceId: String): List<String> {
        return appLogs[serviceId]?.toList() ?: emptyList()
    }
}
