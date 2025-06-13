package com.tigran.domain.usecase

import com.tigran.domain.model.Article
import com.tigran.domain.repo.SearchArticleRepository

class SearchArticleUseCase(private val searchArticleRepository: SearchArticleRepository){
    suspend fun searchArticles(query: String, page: Int): List<Article> {
        return searchArticleRepository.searchArticles(query, page)
    }
}