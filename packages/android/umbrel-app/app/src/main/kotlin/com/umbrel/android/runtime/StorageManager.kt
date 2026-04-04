package com.umbrel.android.runtime

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

class StorageManager(private val context: Context) {
    private val umbrelSharedDir: File by lazy {
        File(Environment.getExternalStorageDirectory(), "Umbrel")
    }

    fun ensureSharedDir(): String {
        if (!umbrelSharedDir.exists()) {
            val success = umbrelSharedDir.mkdirs()
            Log.d("StorageManager", "Created shared Umbrel directory: ${success}")
        }
        return umbrelSharedDir.absolutePath
    }

    fun getStorageInfo(): Pair<Long, Long> {
        val totalSpace = umbrelSharedDir.totalSpace / (1024 * 1024 * 1024)
        val freeSpace = umbrelSharedDir.freeSpace / (1024 * 1024 * 1024)
        return Pair(totalSpace, freeSpace)
    }

    fun buildProotBindMounts(): List<String> {
        // This maps the phone's public 'Umbrel' directory into the proot rootfs as '/home/umbrel'
        val publicUmbrel = ensureSharedDir()
        return listOf("-b", "${publicUmbrel}:/home/umbrel")
    }
}
