package com.tigran.domain.model

data class Article(
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val webUrl: String,
    val publishedDate: String,
)
