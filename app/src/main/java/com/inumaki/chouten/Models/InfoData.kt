package com.inumaki.chouten.Models

data class InfoData(
    val url: String,
    val titles: Titles,
    val altTitles: List<String>,
    val poster: String,
    val banner: String?,
    val description: String,
    val status: Status,
    val rating: Float,
    val yearReleased: Int,
    val mediaType: Int,
    val seasons: List<SeasonData>,
    var mediaList: List<MediaList> = emptyList()
)

enum class Status {
    COMPLETED,
    HIATUS
}

data class SeasonData(
    val name: String,
    val url: String
)

data class MediaList(
    val title: String,
    val pagination: List<Pagination>
)

data class Pagination(
    val id: String,
    val title: String?,
    val items: List<MediaItem>
)

data class MediaItem(
    val url: String,
    val number: Int,
    val title: String?,
    val thumbnail: String?,
    val description: String?
)