package com.inumaki.chouten.Models

import java.util.UUID
import java.util.regex.Pattern

data class DiscoverData(
    val id: UUID = UUID.randomUUID(),
    val url: String,
    val titles: Titles,
    val poster: String,
    val banner: String?,
    val description: String,
    val label: Label,
    val indicator: String?,
    val isWidescreen: Boolean = false,
    val current: Int?,
    val total: Int?
)

//val sanitizedDescription: String
//    get() {
//        val regexPattern = "<[^>]+>"
//        return try {
//            val pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE)
//            val matcher = pattern.matcher(description)
//            matcher.replaceAll("")
//        } catch (e: Exception) {
//            description
//        }
//    }