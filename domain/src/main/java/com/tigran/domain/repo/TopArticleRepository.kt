package com.tigran.domain.repo

import com.tigran.domain.model.Article

interface TopArticleRepository {
    suspend fun getTopArticles(): List<Article>
}