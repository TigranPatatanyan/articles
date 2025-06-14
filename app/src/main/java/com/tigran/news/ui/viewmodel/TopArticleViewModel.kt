package com.tigran.news.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigran.domain.model.Article
import com.tigran.domain.usecase.GetTopArticleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class TopArticlesState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TopArticleViewModel(
    private val getTopArticlesUseCase: GetTopArticleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopArticlesState())
    val uiState: StateFlow<TopArticlesState> = _uiState.asStateFlow()

    init {
        fetchTopArticles()
    }

    fun fetchTopArticles() {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val fetchedArticles = makeApiCallWithRetry {
                    getTopArticlesUseCase.getTopArticles()
                }
                _uiState.value = TopArticlesState(articles = fetchedArticles, isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unknown error occurred while fetching top articles."
                )
            }
        }
    }
}