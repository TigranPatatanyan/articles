package com.tigran.domain.repo

import com.tigran.domain.model.Article

interface SearchArticleRepository {
    suspend fun searchArticles(query: String, page: Int): List<Article>
}