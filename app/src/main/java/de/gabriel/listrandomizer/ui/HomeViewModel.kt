package de.gabriel.listrandomizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gabriel.listrandomizer.data.Item
import de.gabriel.listrandomizer.data.ItemsRepository
import de.gabriel.listrandomizer.data.PhotoSaverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    itemsRepository: ItemsRepository,
    photoSaver: PhotoSaverRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        viewModelScope.launch {
            itemsRepository.getAllItemsWithFiles(photoSaver.photoFolder).map { HomeUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = HomeUiState()
                ).collect { _homeUiState.value = it }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val itemList: List<Item> = listOf())
