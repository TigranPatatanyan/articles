package com.tigran.data.mapper

import com.tigran.data.pojo.Doc
import com.tigran.data.pojo.MostPopularDoc
import com.tigran.domain.model.Article

fun MostPopularDoc.toDomain(): Article {
    val imageUrl = media
        ?.firstOrNull()
        ?.metadata
        ?.lastOrNull()

    return Article(
        id = id.orEmpty(),
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
        id = id.orEmpty(),
        title = headline?.main.orEmpty(),
        snippet = snippet.orEmpty(),
        imageUrl = imageUrl,
        webUrl = webUrl.orEmpty(),
        publishedDate = publishedDate.orEmpty()
    )
}


