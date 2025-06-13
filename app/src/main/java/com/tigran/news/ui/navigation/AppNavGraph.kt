package com.tigran.news.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tigran.news.ui.screen.SearchArticlesScreen
import com.tigran.news.ui.screen.TopArticlesScreen

@Composable
fun AppNavGraph(innerPadding: PaddingValues) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            TopArticlesScreen(onSearchClick = {
                navController.navigate(Screen.Search.route)
            })
        }
        composable(Screen.Search.route) {
            SearchArticlesScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
