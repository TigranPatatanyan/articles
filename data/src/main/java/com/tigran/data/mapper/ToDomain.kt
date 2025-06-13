package com.tigran.data.mapper

import com.tigran.data.pojo.Doc
import com.tigran.data.pojo.MostPopularDoc
import com.tigran.domain.model.Article

fun MostPopularDoc.toDomain(): Article {
    val imageUrl = media
        ?.firstOrNull()
        ?.metadata
        ?.lastOrNull() // usually highest quality last

    return Article(
        title = title.orEmpty(),
        snippet = abstractText.orEmpty(),
        imageUrl = imageUrl?.url,
        webUrl = url.orEmpty(),
        publishedDate = publishedDate.orEmpty()
    )
}

fun Doc.toDomain(): Article {
    val imageUrl = multimedia?.thumbnail?.url ?: multimedia?.default?.url

    return Article(
        title = headline?.main.orEmpty(),
        snippet = snippet.orEmpty(),
        imageUrl = imageUrl,
        webUrl = webUrl.orEmpty(),
        publishedDate = publishedDate.orEmpty()
    )
}


