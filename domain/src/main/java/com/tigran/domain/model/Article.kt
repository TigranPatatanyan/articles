package com.tigran.domain.model

data class Article(
    val id: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val webUrl: String,
    val publishedDate: String,
)
