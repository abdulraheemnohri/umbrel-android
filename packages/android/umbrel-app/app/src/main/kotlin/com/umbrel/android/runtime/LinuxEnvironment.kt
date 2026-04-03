package com.umbrel.android.runtime

import android.content.Context
import android.util.Log
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LinuxEnvironment(private val context: Context) {
    val rootFsDir = File(context.filesDir, "rootfs")
    val dataDir = File(context.filesDir, "data")
    val binDir = File(context.filesDir, "bin")

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (!rootFsDir.exists()) rootFsDir.mkdirs()
        if (!dataDir.exists()) dataDir.mkdirs()
        if (!binDir.exists()) binDir.mkdirs()

        setupMountPoints()
        Log.d("LinuxEnv", "Linux environment initialized at ${rootFsDir.absolutePath}")
    }

    private fun setupMountPoints() {
        // Create necessary directories in rootfs for proot mounting
        val dev = File(rootFsDir, "dev")
        val proc = File(rootFsDir, "proc")
        val sys = File(rootFsDir, "sys")
        val tmp = File(rootFsDir, "tmp")
        val home = File(rootFsDir, "home")

        listOf(dev, proc, sys, tmp, home).forEach { if (!it.exists()) it.mkdirs() }
    }

    fun getInternalPath(appId: String, volumePath: String): String {
        // Maps an app volume path to a persistent Android path
        val appData = File(dataDir, appId)
        if (!appData.exists()) appData.mkdirs()
        return File(appData, volumePath.replace("/", "_")).absolutePath
    }
}
