package com.tigran.news.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigran.domain.model.Article
import com.tigran.domain.usecase.GetTopArticleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TopArticleViewModel(private val getTopArticlesUseCase: GetTopArticleUseCase) : ViewModel() {

    var articles = MutableStateFlow<List<Article>>(emptyList())
    val loading = MutableStateFlow(false)

    fun get() {
        loading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            articles.value = getTopArticlesUseCase.getTopArticles()
            loading.value = false
        }
    }
}