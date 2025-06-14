package com.tigran.news.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigran.domain.model.Article
import com.tigran.domain.usecase.SearchArticleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.text.isBlank

class SearchArticleViewModel(
    private val searchArticleUseCase: SearchArticleUseCase
) : ViewModel() {

    private val _textFieldQuery = MutableStateFlow("")
    val textFieldQuery: StateFlow<String> = _textFieldQuery.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _textFieldQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { debouncedQuery ->
                    if (debouncedQuery.isBlank()) {
                        searchJob?.cancel()
                        _searchState.value = SearchState(
                            query = debouncedQuery
                        )
                    } else {
                        _searchState.value = _searchState.value.copy(
                            query = debouncedQuery,
                            articles = emptyList(),
                            page = 1,
                            loading = true,
                            error = null,
                            canLoadMore = true
                        )
                        performSearch(query = debouncedQuery, pageToFetch = 1, isNewSearch = true)
                    }
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _textFieldQuery.value = newQuery
    }

    fun loadMoreResults() {
        val currentState = _searchState.value
        if (currentState.loading || currentState.query.isBlank() || currentState.error != null || !currentState.canLoadMore) {
            return
        }
        _searchState.value = currentState.copy(loading = true, error = null)
        performSearch(query = currentState.query, pageToFetch = currentState.page, isNewSearch = false)
    }

    private fun performSearch(query: String, pageToFetch: Int, isNewSearch: Boolean) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.value = SearchState()
            return
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val newFetchedArticles = makeApiCallWithRetry {
                    searchArticleUseCase.searchArticles(query, pageToFetch)
                }
                val currentArticles = if (isNewSearch) emptyList() else _searchState.value.articles
                val existingArticleIds = currentArticles.map { it.id }.toSet()
                val uniqueNewArticles = newFetchedArticles.filter { newArticle ->
                    val isUnique = !existingArticleIds.contains(newArticle.id)
                    isUnique
                }

                val updatedArticles = currentArticles + uniqueNewArticles
                val canLoadMoreAfterFetch = newFetchedArticles.isNotEmpty()
                _searchState.value = _searchState.value.copy(
                    articles = updatedArticles,
                    page = pageToFetch + 1,
                    loading = false,
                    error = null,
                    canLoadMore = canLoadMoreAfterFetch
                )
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                } else {
                    _searchState.value = _searchState.value.copy(
                        loading = false,
                        error = e.message ?: "An unknown error occurred",
                        canLoadMore = !isNewSearch
                    )
                }
            }
        }
    }
}


suspend fun <T> makeApiCallWithRetry(
    maxRetries: Int = 3,
    initialDelayMillis: Long = 1000L,
    maxDelayMillis: Long = 8000L,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMillis
    repeat(maxRetries) { attempt ->
        try {
            return block()
        } catch (e: HttpException) {
            if (e.code() == 429) {
                val retryAfterSeconds = e.response()?.headers()?.get("Retry-After")?.toIntOrNull()
                val waitTimeMillis = retryAfterSeconds?.toLong()?.times(1000L) ?: currentDelay
                delay(waitTimeMillis.coerceAtMost(maxDelayMillis))
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            } else {
                throw e
            }
        } catch (e: IOException) {
            if (attempt == maxRetries - 1) throw e
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
        } catch (e: Exception) {
            throw e
        }
    }
    throw IOException("API call failed after $maxRetries retries")
}

@Immutable
data class SearchState(
    val query: String = "",
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val page: Int = 1,
    val error: String? = null,
    val canLoadMore: Boolean = true
)