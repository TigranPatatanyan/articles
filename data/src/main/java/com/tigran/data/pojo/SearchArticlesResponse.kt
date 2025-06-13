package com.tigran.data.pojo

import com.google.gson.annotations.SerializedName

data class SearchArticlesResponse(
    @SerializedName("response")
    val response: SearchResponseBody?
)

data class SearchResponseBody(
    @SerializedName("docs")
    val docs: List<Doc>?
)

data class Doc(
    @SerializedName("headline")
    val headline: Headline?,

    @SerializedName("snippet")
    val snippet: String?,

    @SerializedName("web_url")
    val webUrl: String?,

    @SerializedName("pub_date")
    val publishedDate: String?,

    @SerializedName("multimedia")
    val multimedia: MultimediaObject? = null

)

data class Headline(
    @SerializedName("main")
    val main: String?
)

data class Multimedia(
    @SerializedName("url")
    val url: String?,
    @SerializedName("subtype")
    val subtype: String?
)

data class MultimediaObject(
    @SerializedName("caption")
    val caption: String?,

    @SerializedName("default")
    val default: MultimediaItem?,

    @SerializedName("thumbnail")
    val thumbnail: MultimediaItem?
)

data class MultimediaItem(
    @SerializedName("url")
    val url: String?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("width")
    val width: Int?
)


