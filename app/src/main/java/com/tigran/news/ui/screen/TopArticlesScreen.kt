package com.tigran.news.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tigran.news.ui.viewmodel.TopArticleViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopArticlesScreen(onSearchClick: () -> Unit, viewModel: TopArticleViewModel = koinViewModel()) {
    val articles by viewModel.articles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.get()
    }

    Column {
        TopAppBar(
            title = { Text("Top Articles") },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Articles"
                    )
                }
            }
        )

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            itemsIndexed(articles) { index, article ->
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
}