package com.example.feature.stream

import com.example.feature.minio.MinioService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.minio.errors.ErrorResponseException

class StreamController(
    private val minioService: MinioService
) {
    suspend fun getStream(call: ApplicationCall) {
        call.parameters["videoId"]?.let { videoId ->
            try {
                val objectSize = minioService.getObjectSize(fileId = videoId, contentType = ContentType.Video.MP4)
                call.request.ranges()?.mergeToSingle(objectSize)?.let { range ->
                    call.setResponseRangeHeaders(range, objectSize)

                    call.respondOutputStream(
                        status = HttpStatusCode.PartialContent, contentType = ContentType.Video.MP4
                    ) {
                        minioService.getObjectPart(
                            fileId = videoId,
                            format = ContentType.Video.MP4,
                            offset = range.first,
                            length = range.last - range.first + 1
                        ).use { it.copyTo(this) }
                    }
                }.run {
                    call.respondOutputStream {
                        minioService.getObject(
                            fileName = videoId, fileFormat = ContentType.Video.MP4
                        ).use { it.copyTo(this) }
                    }
                }
            } catch (e: ErrorResponseException) {
                call.respond(HttpStatusCode.fromValue(e.response().code), e.response().message)
            } catch (e: ChannelWriteException) {
                println("channel closed")
            } catch (e: CancellationException) {
                println(e.message)
            }
        }
    }

    private fun ApplicationCall.setResponseRangeHeaders(range: LongRange, objectSize: Long) {
        val contentRange = "bytes ${range.first}-${range.last}/$objectSize"
        this.response.header(
            HttpHeaders.ContentRange, contentRange
        )
        this.response.header(
            HttpHeaders.ContentLength, (range.last - range.first).toString()
        )
    }

    fun getAvailableStreams(): List<AvailableStreamResponse> {
        return minioService.getObjects()?.map {
            AvailableStreamResponse(
                videoId = it.get().objectName().split(".")[0],
                uploadName = it.get().userMetadata()["X-Amz-Meta-Original_name"]
            )
        } ?: emptyList()
    }
}