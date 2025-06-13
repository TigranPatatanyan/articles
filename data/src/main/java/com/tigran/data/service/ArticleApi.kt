package com.tigran.data.service

import com.tigran.data.pojo.SearchArticlesResponse
import com.tigran.data.pojo.TopArticlesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApi {
    @GET("mostpopular/v2/viewed/1.json")
    suspend fun getTopArticles(@Query("api-key") apiKey: String): TopArticlesResponse

    @GET("search/v2/articlesearch.json")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("page") page: Int = 0,
        @Query("api-key") apiKey: String
    ): SearchArticlesResponse
}

const val apiKey = "bbnqlnymPNkHkH7rwhWHyfIJERsqhpTq"
