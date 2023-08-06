package com.example.feature.upload

import com.example.feature.minio.MinioService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureUploadRouting(

) {

    routing {
        val minio = get<MinioService>()

        get("/upload") {
            call.respond(FreeMarkerContent("upload.ftl", model = null))
        }
        post("/upload") {
            val multipartData = call.receiveMultipart()
            val videoIds = mutableListOf<String>()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        if (part.contentType != ContentType.Video.MP4) {
                            call.respond(status = HttpStatusCode.UnsupportedMediaType, message = "illegal format ")
                        }
                        val fileName = part.originalFileName as String
                        videoIds.add(
                            minio.putObject(
                                fileName = fileName,
                                contentType = part.contentType!!,
                                invoke = part.streamProvider.invoke()
                            )
                        )
                    }
                    else -> {part.toString()}
                }
                part.dispose()
            }
            call.respond(videoIds)
        }
    }
}