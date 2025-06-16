package com.example.contentapp.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results")
    val results: List<SearchResult>,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("query")
    val query: String
)

data class SearchResult(
    @SerializedName("id")
    val id: String? = "-",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("subtitle")
    val subtitle: String? = "",
    @SerializedName("image_url")
    val imageUrl: String? = "",
    @SerializedName("content_type")
    val contentType: String? = "book",
    @SerializedName("description")
    val description: String? = "-",
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("duration")
    val duration: String? = "-",
    @SerializedName("episode_number")
    val episodeNumber: Double?,
    @SerializedName("relevance_score")
    val relevanceScore: Float?
)

