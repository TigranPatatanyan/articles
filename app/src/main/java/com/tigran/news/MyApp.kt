package com.tigran.news

import android.app.Application
import com.tigran.news.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                mainModule
            )
        }
    }
}