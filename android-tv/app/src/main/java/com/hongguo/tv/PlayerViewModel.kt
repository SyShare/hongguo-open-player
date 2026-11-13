package com.hongguo.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private val api by lazy { ApiFactory.create() }

    private val _catalog = MutableStateFlow(CatalogResponse())
    val catalog: StateFlow<CatalogResponse> = _catalog.asStateFlow()

    private val _series = MutableStateFlow<SeriesDetail?>(null)
    val series: StateFlow<SeriesDetail?> = _series.asStateFlow()

    fun loadCatalog(page: Int = 1) {
        viewModelScope.launch {
            try {
                _catalog.value = api.getCatalog(page = page)
            } catch (_: Exception) {
                _catalog.value = CatalogResponse()
            }
        }
    }

    fun loadSeries(seriesId: String) {
        viewModelScope.launch {
            try {
                _series.value = api.getSeries(seriesId)
            } catch (_: Exception) {
                _series.value = null
            }
        }
    }
}
