package com.example.contentapp.data.model

import com.google.gson.annotations.SerializedName

data class HomeSectionsResponse(
    @SerializedName("sections")
    val sections: List<Section>,
    @SerializedName("pagination")
    val pagination: Pagination? = null,
)

data class Section(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("name")
    val title: String? = "",
    @SerializedName("type")
    val type: String? = "vertical_list|horizontal",
    @SerializedName("layout")
    val layout: String? = "",
    @SerializedName("order")
    val order: Int? = null,
    @SerializedName("content")
    val items: List<ContentItem>
)

data class ContentItem(
    @SerializedName("audiobook_id")
    val id: String? = "-",
    @SerializedName("name")
    val title: String? = "Nothing",
    @SerializedName("author_name")
    val subtitle: String? = "",
    @SerializedName("avatar_url")
    val imageUrl: String? = "https://media.npr.org/assets/img/2023/03/01/npr-news-now_square.png?s=1400&c=66",
    @SerializedName("content_type")
    val contentType: String? = "book",
    @SerializedName("description")
    val description: String? = "-",
    @SerializedName("author")
    val author: String? = "-",
    @SerializedName("duration")
    val duration: String? = "-",
    @SerializedName("score")
    val episodeNumber: Double?
)

data class Pagination(
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("total_pages")
    val totalPages: Int
)