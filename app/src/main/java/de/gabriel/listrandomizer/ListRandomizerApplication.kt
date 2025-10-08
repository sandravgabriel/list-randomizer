package de.gabriel.listrandomizer

import android.app.Application
import de.gabriel.listrandomizer.data.AppContainer
import de.gabriel.listrandomizer.data.AppDataContainer
import de.gabriel.listrandomizer.data.PhotoSaverRepository

class ListRandomizerApplication : Application() {

    /**
     * AppContainer instance used to obtain dependencies
     */
    lateinit var container: AppContainer
    lateinit var photoSaver: PhotoSaverRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        photoSaver = PhotoSaverRepository(this, this.contentResolver)
    }
}
