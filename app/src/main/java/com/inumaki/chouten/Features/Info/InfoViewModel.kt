package com.inumaki.chouten.Features.Info

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inumaki.chouten.Features.Discover.LoadingState
import com.inumaki.chouten.Models.DiscoverSection
import com.inumaki.chouten.Models.InfoData
import com.inumaki.chouten.Relay.Relay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoViewModel(
    initialUrl: String
) : ViewModel() {
    private val _url = MutableStateFlow(initialUrl)
    val url: StateFlow<String> = _url
    private var state = mutableStateOf(
        LoadingState.INIT
    )

    private val _infoData = MutableStateFlow<InfoData?>(null)
    val infoData: StateFlow<InfoData?> = _infoData
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    private fun loadData() {
        viewModelScope.launch {
            state.value = LoadingState.LOADING
            try {
                val result = withContext(Dispatchers.IO) {
                    println("Running info.")

                    Relay.info(url = url.value) // Runs in a background thread
                }
                println("InfoData: $result")
                _infoData.value = result
                state.value = LoadingState.SUCCESS

                val mediaUrl = infoData.value?.seasons?.first()?.url

                if (mediaUrl != null) {
                    loadMedia(url = mediaUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                state.value = LoadingState.ERROR
            }
        }
    }

    private fun loadMedia(url: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    println("Running media.")

                    Relay.media(url = url) // Runs in a background thread
                }
                println("MediaList: $result")
                _infoData.update { currentValue ->
                    currentValue?.copy(mediaList = result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                state.value = LoadingState.ERROR
            }
        }
    }
}

class InfoViewModelFactory(private val url: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfoViewModel(url) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}