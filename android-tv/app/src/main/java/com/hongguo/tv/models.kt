package com.hongguo.tv

import kotlinx.serialization.Serializable

@Serializable
data class CatalogResponse(
    val items: List<DramaItem> = emptyList(),
    val page: Int = 1,
    val total: Int = 0
)

@Serializable
data class DramaItem(
    val id: String = "",
    val title: String = "",
    val cover: String = "",
    val description: String = "",
    val category: String = ""
)

@Serializable
data class SearchResponse(
    val items: List<DramaItem> = emptyList(),
    val page: Int = 1,
    val total: Int = 0
)

@Serializable
data class SeriesDetail(
    val id: String = "",
    val title: String = "",
    val cover: String = "",
    val description: String = "",
    val episodes: List<Episode> = emptyList()
)

@Serializable
data class Episode(
    val id: String = "",
    val title: String = "",
    val vid: String = "",
    val index: Int = 0
)

@Serializable
data class PlayInfo(
    val vid: String = "",
    val title: String = "",
    val streamUrl: String = "",
    val quality: String = "auto"
)
