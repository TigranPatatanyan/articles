package com.tigran.news.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tigran.news.ui.viewmodel.SearchArticleViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArticlesScreen(
    onBack: () -> Unit,
    viewModel: SearchArticleViewModel = koinViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()
    val textFieldQuery by viewModel.textFieldQuery.collectAsState()

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(searchState.error) {
        searchState.error?.let { errorMessage ->
            if (searchState.articles.isNotEmpty()) {
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = errorMessage,
                        actionLabel = "Retry",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        if (searchState.query.isNotBlank()) {
                            viewModel.loadMoreResults()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = textFieldQuery,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .height(56.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        placeholder = {
                            Text(
                                "Search articles...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            selectionColors = TextSelectionColors(
                                handleColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        trailingIcon = {
                            if (textFieldQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.onQueryChanged("")
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                searchState.loading && searchState.articles.isEmpty() && searchState.query.isNotBlank() -> {
                    FullScreenLoadingState()
                }

                searchState.error != null && searchState.articles.isEmpty() && searchState.query.isNotBlank() -> {
                    FullScreenErrorState(
                        errorMessage = searchState.error ?: "An unknown error occurred.",
                        onRetry = {
                            viewModel.onQueryChanged(searchState.query)
                        }
                    )
                }

                !searchState.loading && searchState.articles.isEmpty() && searchState.query.isNotBlank() && searchState.error == null -> {
                    EmptyContentState(message = "No articles found for \"${searchState.query}\".")
                }

                searchState.query.isBlank() && searchState.articles.isEmpty() && !searchState.loading && searchState.error == null -> {
                    EmptyContentState(message = "Type to search for news articles.")
                }

                searchState.articles.isNotEmpty() -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                    ) {
                        itemsIndexed(
                            items = searchState.articles,
                            key = { _, article -> article.id }
                        ) { index, article ->
                            ArticleItem(
                                article = article,
                            )
                        }

                        if (searchState.loading && searchState.articles.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(strokeWidth = 3.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            if (totalItemsCount == 0) return@derivedStateOf false
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val threshold = 5
            val trigger = lastVisibleItemIndex >= totalItemsCount - 1 - threshold
            trigger && searchState.canLoadMore && !searchState.loading && searchState.query.isNotBlank()
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreResults()
        }
    }
}


@Composable
fun FullScreenLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Searching news...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FullScreenErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops! Something went wrong.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyContentState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}