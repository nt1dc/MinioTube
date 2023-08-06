package com.example.feature.upload

import com.example.feature.minio.MinioService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UploadController(
    private val minioService: MinioService
) {
    suspend fun upload(call: ApplicationCall): MutableList<String> {
        val videoIds = mutableListOf<String>()
        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    if (part.contentType != ContentType.Video.MP4) {
                        call.respond(status = HttpStatusCode.UnsupportedMediaType, message = "illegal format ")
                    }
                    val fileName = part.originalFileName as String
                    val objectId = minioService.putObject(
                        fileName = fileName,
                        contentType = part.contentType!!,
                        invoke = part.streamProvider.invoke()
                    )
                    videoIds.add(objectId)
                }

                else -> {
                    part.dispose
                }
            }
            part.dispose()
        }
        return videoIds
    }
}