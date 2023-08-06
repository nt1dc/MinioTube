package com.example.feature.upload

import com.example.feature.minio.MinioService
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureUploadRouting(

) {

    routing {
        val uploadController = UploadController(get<MinioService>())
        route("/upload") {
            get("") {
                call.respond(FreeMarkerContent("upload.ftl", model = null))
            }

            post("") {
                call.respond(uploadController.upload(call))
            }
        }
    }
}