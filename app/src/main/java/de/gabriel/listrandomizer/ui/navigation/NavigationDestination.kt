package de.gabriel.listrandomizer.ui.navigation

/**
 * Interface to describe the navigation destinations for the app
 */
interface NavigationDestination {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id that contains title to be displayed for the screen.
     */
    val titleRes: Int
}
