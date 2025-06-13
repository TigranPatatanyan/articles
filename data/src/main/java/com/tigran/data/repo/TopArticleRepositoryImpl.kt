package com.tigran.data.repo

import com.tigran.data.mapper.toDomain
import com.tigran.data.service.ArticleApi
import com.tigran.data.service.apiKey
import com.tigran.domain.model.Article
import com.tigran.domain.repo.TopArticleRepository

class TopArticleRepositoryImpl(private val apiService: ArticleApi): TopArticleRepository {
    override suspend fun getTopArticles(): List<Article> {
        return apiService.getTopArticles(apiKey).results?.map { it.toDomain() }?: emptyList()
    }

}