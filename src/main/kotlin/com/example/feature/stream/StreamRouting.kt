package com.example.feature.stream

import com.example.feature.minio.MinioService
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get


fun Application.configureStreamRouting() {
    val streamController = StreamController(get<MinioService>())
    routing {
        route("/streams") {
            get("") {
                call.respond(streamController.getAvailableStreams())
            }
            get("/{videoId}") {
                streamController.getStream(call)
            }
        }

        get("/watch/{videoId}") {
            call.parameters["videoId"]?.let {
                call.respond(FreeMarkerContent("watch.ftl", mapOf("videoId" to it)))
            }
        }
    }
}