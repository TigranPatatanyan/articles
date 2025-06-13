package com.tigran.news.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tigran.news.ui.viewmodel.SearchArticleViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArticlesScreen(onBack: () -> Unit, viewModel: SearchArticleViewModel = koinViewModel()) {
    val query = remember { mutableStateOf("") }
    val articles by viewModel.articles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val listState = rememberLazyListState()

    Column {
        TopAppBar(
            title = {
                TextField(
                    value = query.value,
                    onValueChange = {
                        query.value = it
                        viewModel.search(it, reset = true)
                    },
                    placeholder = { Text("Search...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            itemsIndexed(articles) { _, article ->
                ArticleItem(article)
            }

            if (loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= articles.size - 3 && !loading
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.search(query.value, reset = false)
        }
    }
}