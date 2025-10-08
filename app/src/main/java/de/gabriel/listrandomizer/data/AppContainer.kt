package de.gabriel.listrandomizer.data

import android.content.Context

interface AppContainer {
    val itemsRepository: ItemsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(ListRandomizerDatabase.getDatabase(context).itemDao())
    }
}
