package com.umbrel.android.core.parser

import android.util.Log
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

data class AndroidServiceConfig(
    val name: String,
    val image: String,
    val command: String?,
    val ports: List<Int>,
    val volumes: List<Pair<String, String>>, // Mapping of internal to external
    val environment: Map<String, String>,
    val dependsOn: List<String> = emptyList()
)

class ComposeParser {
    fun parseCompose(inputStream: InputStream): List<AndroidServiceConfig> {
        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(inputStream)

        val services = data["services"] as? Map<String, Any> ?: return emptyList()
        val result = mutableListOf<AndroidServiceConfig>()

        for ((name, config) in services) {
            val serviceMap = config as? Map<String, Any> ?: continue
            val image = serviceMap["image"] as? String ?: continue
            val command = serviceMap["command"] as? String
            val ports = parsePorts(serviceMap["ports"])
            val volumes = parseVolumes(serviceMap["volumes"])
            val environment = parseEnvironment(serviceMap["environment"])
            val dependsOn = parseDependsOn(serviceMap["depends_on"])

            result.add(
                AndroidServiceConfig(
                    name = name,
                    image = image,
                    command = command,
                    ports = ports,
                    volumes = volumes,
                    environment = environment,
                    dependsOn = dependsOn
                )
            )
        }

        return result
    }

    private fun parsePorts(ports: Any?): List<Int> {
        return when (ports) {
            is List<*> -> ports.mapNotNull { it?.toString()?.split(":")?.firstOrNull()?.toIntOrNull() }
            else -> emptyList()
        }
    }

    private fun parseVolumes(volumes: Any?): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        if (volumes is List<*>) {
            for (volume in volumes) {
                val parts = volume.toString().split(":")
                if (parts.size >= 2) {
                    result.add(Pair(parts[0], parts[1]))
                }
            }
        }
        return result
    }

    private fun parseEnvironment(env: Any?): Map<String, String> {
        val result = mutableMapOf<String, String>()
        when (env) {
            is Map<*, *> -> {
                for ((key, value) in env) {
                    result[key.toString()] = value.toString()
                }
            }
            is List<*> -> {
                for (item in env) {
                    val parts = item.toString().split("=", limit = 2)
                    if (parts.size == 2) {
                        result[parts[0]] = parts[1]
                    }
                }
            }
        }
        return result
    }

    private fun parseDependsOn(depends: Any?): List<String> {
        return when (depends) {
            is List<*> -> depends.mapNotNull { it?.toString() }
            is Map<*, *> -> depends.keys.map { it.toString() }
            else -> emptyList()
        }
    }
}
