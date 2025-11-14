// Vị trí: .../viewmodel/MapViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.StoreLocationDto
import com.example.salesapp.data.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val isLoading: Boolean = true,
    val locations: List<StoreLocationDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    var uiState by mutableStateOf(MapUiState())
        private set

    init {
        fetchLocations()
    }

    fun fetchLocations() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = mapRepository.getStoreLocations()
            result.fold(
                onSuccess = { locs ->
                    uiState = uiState.copy(isLoading = false, locations = locs)
                },
                onFailure = { err ->
                    uiState = uiState.copy(isLoading = false, errorMessage = err.message)
                }
            )
        }
    }
}