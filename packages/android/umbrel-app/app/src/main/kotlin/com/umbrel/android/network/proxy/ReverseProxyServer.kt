package com.umbrel.android.network.proxy

import android.util.Log
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

class ReverseProxyServer(private val port: Int) {
    private val client = HttpClient(CIO)
    private var server: NettyApplicationEngine? = null

    fun start() {
        server = embeddedServer(Netty, port = port) {
            routing {
                get("/{...}") {
                    val path = call.request.uri
                    Log.d("ProxyServer", "Routing request for ${path}")

                    // Route mapping: appId -> internalPort
                    val targetUrl = mapToInternalService(path)

                    if (targetUrl != null) {
                        try {
                            val response: HttpResponse = client.get(targetUrl)
                            call.respond(response.status, response.readBytes())
                        } catch (e: Exception) {
                            Log.e("ProxyServer", "Failed to proxy request", e)
                            call.respond(HttpStatusCode.ServiceUnavailable, "Internal service unavailable")
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No matching service found")
                    }
                }
            }
        }.start(wait = false)
        Log.i("ProxyServer", "Network Gateway started on port ${port}")
    }

    fun stop() {
        server?.stop(1000, 2000)
        Log.i("ProxyServer", "Network Gateway stopped")
    }

    private fun mapToInternalService(path: String): String? {
        // Simplified mapping logic: /bitcoin -> localhost:8332
        if (path.startsWith("/bitcoin")) return "http://localhost:8332"
        if (path.startsWith("/nextcloud")) return "http://localhost:8080"
        return null
    }
}
