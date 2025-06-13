package com.tigran.data.pojo

import com.google.gson.annotations.SerializedName


data class TopArticlesResponse(
    val results: List<MostPopularDoc>?
)

data class MostPopularDoc(
    @SerializedName("title")
    val title: String?,

    @SerializedName("abstract")
    val abstractText: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("published_date")
    val publishedDate: String?,

    @SerializedName("media")
    val media: List<Media>?
)

data class Media(
    @SerializedName("media-metadata")
    val metadata: List<MediaMetadata>?
)

data class MediaMetadata(
    val url: String?
)
