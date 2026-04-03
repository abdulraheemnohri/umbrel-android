package com.umbrel.android.runtime

import android.content.Context
import android.util.Log
import com.umbrel.android.runtime.service.ServiceLifecycleManager
import com.umbrel.android.runtime.service.ServiceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

enum class RuntimeState {
    STOPPED,
    INITIALIZING,
    RUNNING,
    ERROR
}

class RuntimeManager(private val context: Context) {
    private val _state = MutableStateFlow(RuntimeState.STOPPED)
    val state: StateFlow<RuntimeState> = _state

    val linuxEnv = LinuxEnvironment(context)
    private val prootRunner by lazy { ProotRunner(linuxEnv.rootFsDir, linuxEnv.binDir) }
    val lifecycleManager by lazy { ServiceLifecycleManager(prootRunner) }
    val supervisor by lazy { BackgroundSupervisor(lifecycleManager) }

    suspend fun start() {
        if (_state.value == RuntimeState.RUNNING || _state.value == RuntimeState.INITIALIZING) return

        _state.value = RuntimeState.INITIALIZING
        try {
            Log.d("RuntimeManager", "Starting Umbrel Runtime...")
            linuxEnv.initialize()

            // Start core services (e.g., system-level background processes)
            supervisor.startMonitoring()

            _state.value = RuntimeState.RUNNING
        } catch (e: Exception) {
            Log.e("RuntimeManager", "Failed to start runtime", e)
            _state.value = RuntimeState.ERROR
        }
    }

    suspend fun stop() {
        if (_state.value == RuntimeState.STOPPED) return

        Log.d("RuntimeManager", "Stopping Umbrel Runtime...")
        supervisor.stopAll()
        _state.value = RuntimeState.STOPPED
    }
}
