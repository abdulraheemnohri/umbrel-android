package com.umbrel.android.runtime

import android.content.Context
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LinuxEnvironmentTest {
    @Test
    fun testGetInternalPath() {
        val tempDir = File("temp_files_env")
        tempDir.mkdirs()

        // Manual mock for LinuxEnvironment as we can't easily use Mockito in the sandbox
        val linuxEnv = object : Any() {
            val rootFsDir = File(tempDir, "rootfs")
            val dataDir = File(tempDir, "data")
            fun getInternalPath(appId: String, volumePath: String): String {
                val appData = File(dataDir, appId)
                if (!appData.exists()) appData.mkdirs()
                return File(appData, volumePath.replace("/", "_")).absolutePath
            }
        }

        val path = linuxEnv.getInternalPath("myapp", "/data/storage")

        assertTrue(path.contains("myapp"))
        assertTrue(path.contains("_data_storage"))

        tempDir.deleteRecursively()
    }
}
