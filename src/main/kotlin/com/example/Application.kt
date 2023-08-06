package com.example

import com.example.feature.minio.MinioConfig
import com.example.feature.minio.MinioConfigEnv
import com.example.feature.minio.MinioService
import com.example.feature.stream.configureStreamRouting
import com.example.feature.upload.configureUploadRouting
import freemarker.cache.ClassTemplateLoader
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.*
import io.minio.MinioClient
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val minioServiceComponent = module {
    single<MinioConfig> {
        MinioConfigEnv()
    }
    single<MinioClient> {
        with(get<MinioConfig>()) {
            MinioClient.builder()
                .endpoint(host, port, secure)
                .credentials(minioRootUser, minioRootPassword)
                .build()
        }
    }
    single {
        MinioService(
            get<MinioClient>(), bucketName = "videos"
        )
    }
}

fun Application.module() {
    install(PartialContent)
    install(ContentNegotiation) {
        json()
    }


    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(Koin) {
        modules(minioServiceComponent)
    }
    configureUploadRouting()
    configureStreamRouting()
}
