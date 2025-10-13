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

class HomeViewModel(
    itemsRepository: ItemsRepository,
    photoSaver: PhotoSaverRepository
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow<String?>(null)
    private val _playerCount = MutableStateFlow("") // Kept as String for the TextField

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
            _allGenres
        ) { genre, count, genres ->
            Triple(genre, count, genres)
        }.flatMapLatest { (genre, countStr, genres) ->
            val playerCount = countStr.toIntOrNull()
            itemsRepository.getFilteredItemsWithFiles(
                genre = genre,
                playerCount = playerCount,
                file = photoSaver.photoFolder
            ).map { items ->
                HomeUiState(
                    itemList = items,
                    allGenres = genres,
                    selectedGenre = genre,
                    playerCountFilter = countStr,
                    isFilterActive = genre != null || playerCount != null
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    fun updateGenreFilter(genre: String) {
        _selectedGenre.value = if (_selectedGenre.value == genre) null else genre
    }

    fun updatePlayerCountFilter(count: String) {
        _playerCount.value = count
    }

    fun clearFilters() {
        _selectedGenre.value = null
        _playerCount.value = ""
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
    val isFilterActive: Boolean = false
)
