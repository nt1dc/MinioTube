package com.example.feature.minio

import io.ktor.http.*
import io.minio.*
import io.minio.messages.Item
import java.io.InputStream
import java.util.*


class MinioService(
    private val minioClient: MinioClient,
    private val bucketName: String
) {



    init {
        setupBucket()
    }

    private fun setupBucket() {
        val found: Boolean = isBucketExist()
        if (!found) {
            createBucket()
        }
    }

    private fun createBucket() {
        minioClient.makeBucket(
            MakeBucketArgs.builder()
                .bucket(bucketName).build()
        )
    }


    private fun isBucketExist() =
        minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())


    fun getObjectPart(fileId: String, format: ContentType, offset: Long, length: Long): GetObjectResponse =
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(getFileName(fileId, format))
                .offset(offset)
                .length(length)
                .build()
        )

    fun getObject(fileName: String, fileFormat: ContentType): GetObjectResponse =
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(getFileName(fileName, fileFormat))
                .build()
        )


    fun getObjectSize(fileId: String, contentType: ContentType) =
        minioClient.statObject(
            StatObjectArgs.builder()
                .bucket(bucketName)
                .`object`(getFileName(fileId, contentType))
                .build()
        ).size()


    fun putObject(fileName: String, contentType: ContentType, invoke: InputStream): String {
        val objectId = UUID.randomUUID().toString()
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`("${objectId}.${contentType.contentSubtype}")
                .stream(invoke, -1, 10_485_760)
                .contentType("${contentType.contentType}/${contentType.contentSubtype}")
                .userMetadata(mapOf("original_name" to fileName))
                .build()
        )
        return objectId
    }

    fun getObjects(): MutableIterable<Result<Item>>? =
        minioClient.listObjects(
            ListObjectsArgs.builder()
                .includeUserMetadata(true)
                .bucket(bucketName)
                .build()
        )

    private fun getFileName(objectId: String, fileFormat: ContentType) =
        "${objectId}.${fileFormat.contentSubtype}"
}
