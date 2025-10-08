package de.gabriel.listrandomizer.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.gabriel.listrandomizer.ListRandomizerApplication
import de.gabriel.listrandomizer.ui.item.ItemDetailsViewModel
import de.gabriel.listrandomizer.ui.item.ItemEditViewModel
import de.gabriel.listrandomizer.ui.item.ItemEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                randomizerApplication().container.itemsRepository,
                randomizerApplication().photoSaver
            )
        }

        initializer {
            ItemEntryViewModel(
                randomizerApplication().container.itemsRepository,
                randomizerApplication().photoSaver,
            )
        }

        initializer {
            ItemDetailsViewModel(
                randomizerApplication().container.itemsRepository,
                randomizerApplication().photoSaver
            )
        }

        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle(),
                randomizerApplication().container.itemsRepository,
                randomizerApplication().photoSaver
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ListRandomizerApplication].
 */
fun CreationExtras.randomizerApplication(): ListRandomizerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ListRandomizerApplication)
