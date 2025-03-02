package com.inumaki.chouten.Models

data class Response(
    val statusCode: Int,
    val headers: Map<String, String>,
    val contentType: String,
    val body: String
)