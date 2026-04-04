package com.umbrel.android.runtime

import java.io.File

class ProotRunner(
    private val rootDir: File,
    private val binDir: File,
    private val customMounts: List<String> = emptyList()
) {
    fun buildCommand(command: String): List<String> {
        val prootBin = File(binDir, "proot").absolutePath
        val baseCommand = mutableListOf(
            prootBin,
            "-r", rootDir.absolutePath,
            "-b", "/dev",
            "-b", "/proc",
            "-b", "/sys"
        )
        baseCommand.addAll(customMounts)
        baseCommand.addAll(listOf("/bin/sh", "-c", command))
        return baseCommand
    }

    fun execute(command: String): Process {
        val fullCommand = buildCommand(command)
        return ProcessBuilder(fullCommand)
            .redirectErrorStream(true)
            .start()
    }
}
