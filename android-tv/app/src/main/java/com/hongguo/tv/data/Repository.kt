package com.hongguo.tv.data

import com.hongguo.tv.CatalogResponse
import com.hongguo.tv.PlayInfo
import com.hongguo.tv.SearchResponse
import com.hongguo.tv.SeriesDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HongguoRepository(
    private val api: ApiService
) {
    suspend fun loadCatalog(page: Int = 1): CatalogResponse = withContext(Dispatchers.IO) {
        api.getCatalog(page = page)
    }

    suspend fun search(keyword: String, page: Int = 1): SearchResponse = withContext(Dispatchers.IO) {
        api.search(keyword, page)
    }

    suspend fun loadSeries(seriesId: String): SeriesDetail = withContext(Dispatchers.IO) {
        api.getSeries(seriesId)
    }

    suspend fun loadPlay(vid: String, quality: String = "auto"): PlayInfo = withContext(Dispatchers.IO) {
        api.getPlay(vid, quality)
    }
}
