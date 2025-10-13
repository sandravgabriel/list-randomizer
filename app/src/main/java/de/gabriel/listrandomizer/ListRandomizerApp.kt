package de.gabriel.listrandomizer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.gabriel.listrandomizer.ui.HomeDestination
import de.gabriel.listrandomizer.ui.HomeScreen
import de.gabriel.listrandomizer.ui.common.TopAppBar
import de.gabriel.listrandomizer.ui.item.ItemDetailsDestination
import de.gabriel.listrandomizer.ui.item.ItemDetailsScreen
import de.gabriel.listrandomizer.ui.item.ItemEditDestination
import de.gabriel.listrandomizer.ui.item.ItemEditScreen
import de.gabriel.listrandomizer.ui.item.ItemEntryDestination
import de.gabriel.listrandomizer.ui.item.ItemEntryScreen
import de.gabriel.listrandomizer.ui.navigation.DetailPaneState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListRandomizerApp(
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val listDetailPaneNavigator = rememberListDetailPaneScaffoldNavigator<DetailPaneState>()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = HomeDestination.route) {
        composable(HomeDestination.route) {
            NavigableListDetailPaneScaffold(
                modifier = modifier,
                navigator = listDetailPaneNavigator,
                listPane = {
                    AnimatedPane(modifier = Modifier) {
                        Column {
                            TopAppBar(
                                title = stringResource(R.string.list_title),
                                canNavigateBack = false
                            )
                            HomeScreen(
                                navigateToItemEntry = {
                                    navController.navigate(ItemEntryDestination.route)
                                },
                                onItemClick = { itemId ->
                                    scope.launch {
                                        listDetailPaneNavigator.navigateTo(
                                            ListDetailPaneScaffoldRole.Detail,
                                            DetailPaneState.ViewItem(itemId)
                                        )
                                    }
                                },
                                provideScaffold = false
                            )
                        }
                    }
                },
                detailPane = {
                    AnimatedPane(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = listDetailPaneNavigator.currentDestination?.contentKey,
                            label = "DetailPaneAnimation"
                        ) { currentPaneState: DetailPaneState? ->
                            when (currentPaneState) {
                                is DetailPaneState.ViewItem -> {
                                    ItemDetailsScreen(
                                        selectedItemIdFromParent = currentPaneState.itemId,
                                        onClosePane = { 
                                            scope.launch {
                                                listDetailPaneNavigator.navigateBack()
                                            }
                                        },
                                        navigateToEditItem = { itemId -> 
                                            scope.launch {
                                                listDetailPaneNavigator.navigateTo(
                                                    ListDetailPaneScaffoldRole.Detail,
                                                    DetailPaneState.EditItem(itemId)
                                                )
                                            }
                                        },
                                        navigateBack = { 
                                            scope.launch {
                                                listDetailPaneNavigator.navigateBack()
                                            }
                                        },
                                        provideScaffold = false,
                                        itemIdFromNavArgs = null
                                    )
                                }
                                is DetailPaneState.EditItem -> {
                                    ItemEditScreen(
                                        itemIdFromPane = currentPaneState.itemId,
                                        onDoneEditingInPane = { editedItemId ->
                                            scope.launch {
                                                listDetailPaneNavigator.navigateTo(
                                                    ListDetailPaneScaffoldRole.Detail,
                                                    DetailPaneState.ViewItem(editedItemId)
                                                )
                                            }
                                        },
                                        onNavigateBackInPane = { 
                                            val itemId = (listDetailPaneNavigator.currentDestination?.contentKey as? DetailPaneState.EditItem)?.itemId
                                            if (itemId != null) {
                                                scope.launch {
                                                    listDetailPaneNavigator.navigateTo(
                                                        ListDetailPaneScaffoldRole.Detail,
                                                        DetailPaneState.ViewItem(itemId)
                                                    )
                                                }
                                            } else {
                                                scope.launch { listDetailPaneNavigator.navigateBack() } 
                                            }
                                        },
                                        provideScaffold = false,
                                        topAppBarTitleText = stringResource(R.string.edit_item_title),
                                        navigateBack = { navController.popBackStack() }, 
                                        onNavigateUp = { navController.navigateUp() } 
                                    )
                                }
                                is DetailPaneState.Hidden, null -> {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        TopAppBar(
                                            title = "Details",
                                            canNavigateBack = false
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "Select an item to view its details.")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
            
            val isDetailPaneShowingContent = listDetailPaneNavigator.currentDestination?.contentKey.let { state ->
                state is DetailPaneState.ViewItem || state is DetailPaneState.EditItem
            }
            BackHandler(enabled = isDetailPaneShowingContent) {
                scope.launch {
                    val currentKey = listDetailPaneNavigator.currentDestination?.contentKey
                    when (currentKey) {
                        is DetailPaneState.EditItem -> {
                            listDetailPaneNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                DetailPaneState.ViewItem(currentKey.itemId)
                            )
                        }
                        is DetailPaneState.ViewItem -> {
                            listDetailPaneNavigator.navigateBack()
                        }
                        else -> {
                            listDetailPaneNavigator.navigateBack()
                        }
                    }
                }
            }
        }

        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.ITEM_ID_ARG) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt(ItemDetailsDestination.ITEM_ID_ARG)
            ItemDetailsScreen(
                itemIdFromNavArgs = itemId,
                selectedItemIdFromParent = null,
                onClosePane = null, 
                navigateToEditItem = { currentItemId ->
                    navController.navigate("${ItemEditDestination.route}/$currentItemId")
                },
                navigateBack = { navController.popBackStack() },
                provideScaffold = true
            )
        }

        composable(
            route = ItemEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemEditDestination.ITEM_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            ItemEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                provideScaffold = true
            )
        }
    }
}
