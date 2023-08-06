package com.example.feature.minio

interface MinioConfig {
    val host: String
    val port: Int
    val minioRootUser: String
    val minioRootPassword: String
    val secure: Boolean
}