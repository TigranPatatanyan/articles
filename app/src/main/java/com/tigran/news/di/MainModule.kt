package com.tigran.news.di

import com.tigran.data.repo.SearchArticleRepositoryImpl
import com.tigran.data.repo.TopArticleRepositoryImpl
import com.tigran.data.service.ArticleApi
import com.tigran.domain.repo.SearchArticleRepository
import com.tigran.domain.repo.TopArticleRepository
import com.tigran.domain.usecase.GetTopArticleUseCase
import com.tigran.domain.usecase.SearchArticleUseCase
import com.tigran.news.ui.viewmodel.SearchArticleViewModel
import com.tigran.news.ui.viewmodel.TopArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mainModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ArticleApi> { get<Retrofit>().create(ArticleApi::class.java) }
    viewModel {
        TopArticleViewModel(get<GetTopArticleUseCase>())
    }
    viewModel { SearchArticleViewModel(get<SearchArticleUseCase>()) }
    factory { GetTopArticleUseCase(get()) }
    factory { SearchArticleUseCase(get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SearchArticleRepository> { SearchArticleRepositoryImpl(get()) }
}