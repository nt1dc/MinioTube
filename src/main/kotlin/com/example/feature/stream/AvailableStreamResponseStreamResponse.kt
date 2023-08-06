package com.example.feature.stream

import kotlinx.serialization.Serializable

@Serializable
data class AvailableStreamResponse(
    val videoId: String?,
    val uploadName: String?
)