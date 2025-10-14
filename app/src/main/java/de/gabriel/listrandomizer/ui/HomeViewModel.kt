package de.gabriel.listrandomizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gabriel.listrandomizer.data.Item
import de.gabriel.listrandomizer.data.ItemsRepository
import de.gabriel.listrandomizer.data.PhotoSaverRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(
    itemsRepository: ItemsRepository,
    photoSaver: PhotoSaverRepository
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow<String?>(null)
    private val _playerCount = MutableStateFlow("") // Kept as String for the TextField
    private val _randomlySelectedItem = MutableStateFlow<Item?>(null)

    private val _allGenres: StateFlow<List<String>> =
        itemsRepository.getAllGenres()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            _selectedGenre,
            _playerCount,
            _allGenres,
            _randomlySelectedItem
        ) { genre, count, genres, randomItem ->
            // This combination logic primarily serves to trigger the flatMapLatest block
            // when any of the source flows emit a new value.
            HomeUiState(
                allGenres = genres,
                selectedGenre = genre,
                playerCountFilter = count,
                isFilterActive = genre != null || count.isNotBlank(),
                randomlySelectedItem = randomItem
            )
        }.flatMapLatest { state ->
            // This is the main block for fetching and mapping the final UI state.
            // It re-executes whenever the combined state changes.
            val playerCount = state.playerCountFilter.toIntOrNull()
            itemsRepository.getFilteredItemsWithFiles(
                genre = state.selectedGenre,
                playerCount = playerCount,
                file = photoSaver.photoFolder
            ).map { items ->
                // We update the state with the latest items list while preserving other state values.
                state.copy(itemList = items)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    fun updateGenreFilter(genre: String) {
        _selectedGenre.update { if (it == genre) null else genre }
    }

    fun updatePlayerCountFilter(count: String) {
        _playerCount.value = count
    }

    fun clearFilters() {
        _selectedGenre.value = null
        _playerCount.value = ""
    }

    fun pickRandomItem() {
        if (homeUiState.value.itemList.isNotEmpty()) {
            _randomlySelectedItem.value = homeUiState.value.itemList.random()
        }
    }

    fun clearRandomItem() {
        _randomlySelectedItem.value = null
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(
    val itemList: List<Item> = listOf(),
    val allGenres: List<String> = listOf(),
    val selectedGenre: String? = null,
    val playerCountFilter: String = "",
    val isFilterActive: Boolean = false,
    val randomlySelectedItem: Item? = null
)
