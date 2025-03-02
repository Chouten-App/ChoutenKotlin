package com.inumaki.chouten.Features.Discover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inumaki.chouten.Models.DiscoverSection
import com.inumaki.chouten.Relay.Relay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LoadingState {
    INIT, LOADING, SUCCESS, ERROR
}

class DiscoverViewModel: ViewModel() {
    var state = mutableStateOf(
        LoadingState.INIT
    )

    private val _sections = MutableStateFlow<List<DiscoverSection>?>(null)
    val sections: StateFlow<List<DiscoverSection>?> = _sections
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    private fun loadData() {
        viewModelScope.launch {
            state.value = LoadingState.LOADING
            try {
                val result = withContext(Dispatchers.IO) {
                    Relay.discover() // Runs in a background thread
                }
                println("SECTIONS: $result")
                _sections.value = result
                state.value = LoadingState.SUCCESS
            } catch (e: Exception) {
                e.printStackTrace()
                state.value = LoadingState.ERROR
            }
        }
    }
}