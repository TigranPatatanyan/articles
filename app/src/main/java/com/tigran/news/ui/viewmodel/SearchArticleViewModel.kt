package com.tigran.news.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigran.domain.model.Article
import com.tigran.domain.usecase.SearchArticleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchArticleViewModel(private val searchArticleUseCase: SearchArticleUseCase, ): ViewModel(){
    private var currentPage = 0
    private var currentQuery = ""

    val articles = MutableStateFlow<List<Article>>(emptyList())
    val loading = MutableStateFlow(false)
    private var searchJob: Job? = null


    fun search(query: String, reset: Boolean = false) {
        if (loading.value) return

        if (reset) {
            searchJob?.cancel()
            currentQuery = query
            currentPage = 0
            articles.value = emptyList()
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val newArticles = searchArticleUseCase.searchArticles(currentQuery, currentPage)
                articles.value += newArticles
                currentPage++
            } finally {
                loading.value = false
            }
        }
    }
}