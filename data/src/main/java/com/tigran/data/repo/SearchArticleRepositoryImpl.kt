package com.tigran.data.repo

import com.tigran.data.mapper.toDomain
import com.tigran.data.service.ArticleApi
import com.tigran.data.service.apiKey
import com.tigran.domain.model.Article
import com.tigran.domain.repo.SearchArticleRepository

class SearchArticleRepositoryImpl(private val apiService: ArticleApi) : SearchArticleRepository {
    override suspend fun searchArticles(query: String, page: Int): List<Article> {
        return apiService.searchArticles(query, page, apiKey)
            .response
            ?.docs
            ?.map { it.toDomain() }
            .orEmpty()
    }
}