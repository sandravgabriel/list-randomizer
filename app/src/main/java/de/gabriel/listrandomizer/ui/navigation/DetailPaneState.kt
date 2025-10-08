package de.gabriel.listrandomizer.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DetailPaneState : Parcelable {
    @Parcelize
    data object Hidden : DetailPaneState()

    @Parcelize
    data class ViewItem(val itemId: Int) : DetailPaneState()

    @Parcelize
    data class EditItem(val itemId: Int) : DetailPaneState()
}
