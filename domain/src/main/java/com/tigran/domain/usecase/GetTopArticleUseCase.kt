package com.tigran.domain.usecase

import com.tigran.domain.model.Article
import com.tigran.domain.repo.TopArticleRepository

class GetTopArticleUseCase(private val topArticleRepository: TopArticleRepository){
    suspend fun getTopArticles(): List<Article> {
        return topArticleRepository.getTopArticles()
    }
}