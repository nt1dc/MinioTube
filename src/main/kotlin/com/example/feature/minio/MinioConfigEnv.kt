package com.example.feature.minio

class MinioConfigEnv : MinioConfig {
    override val host: String
        get() = System.getenv("MINIO_HOST") ?: "localhost"
    override val port: Int
        get() = System.getenv("MINIO_PORT")?.toInt() ?: 9000
    override val minioRootUser: String
        get() = System.getenv("MINIO_ROOT_USER")
    override val minioRootPassword: String
        get() = System.getenv("MINIO_ROOT_PASSWORD")
    override val secure: Boolean
        get() = false
}