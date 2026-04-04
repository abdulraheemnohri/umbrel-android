package com.umbrel.android.runtime

import android.util.Log
import com.umbrel.android.runtime.service.ServiceInfo
import com.umbrel.android.runtime.service.ServiceLifecycleManager
import java.io.File

class SambaManager(
    private val linuxEnv: LinuxEnvironment,
    private val lifecycleManager: ServiceLifecycleManager
) {
    private val configPath = File(linuxEnv.rootFsDir, "etc/samba/smb.conf")

    fun generateConfig(sharedPath: String) {
        Log.d("SambaManager", "Generating Samba config for ${sharedPath}")

        val config = """
            [global]
               workgroup = WORKGROUP
               server string = Umbrel Android File Server
               security = user
               map to guest = Bad User
               log file = /var/log/samba/%m.log
               max log size = 50

            [Shared]
               path = ${sharedPath}
               public = yes
               writable = yes
               guest ok = yes
               create mask = 0777
               directory mask = 0777
        """.trimIndent()

        File(configPath.parent).mkdirs()
        configPath.writeText(config)
    }

    fun start() {
        val smbdService = ServiceInfo(
            id = "system_samba",
            name = "Samba File Server",
            command = "smbd -F --no-process-group --configfile=${configPath.absolutePath}"
        )
        lifecycleManager.startService(smbdService)
    }

    fun stop() {
        lifecycleManager.stopService("system_samba")
    }
}
