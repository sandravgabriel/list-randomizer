package de.gabriel.listrandomizer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.gabriel.listrandomizer.data.Item
import de.gabriel.listrandomizer.ui.AppViewModelProvider
import de.gabriel.listrandomizer.ui.HomeDestination
import de.gabriel.listrandomizer.ui.HomeScreen
import de.gabriel.listrandomizer.ui.HomeUiState
import de.gabriel.listrandomizer.ui.HomeViewModel
import de.gabriel.listrandomizer.ui.common.TopAppBar
import de.gabriel.listrandomizer.ui.item.ItemDetails
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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column {
                                TopAppBar(
                                    title = stringResource(R.string.list_title),
                                    canNavigateBack = false,
                                    actions = {
                                        IconButton(onClick = { viewModel.pickRandomItem() }) {
                                            Icon(
                                                Icons.Default.Casino,
                                                contentDescription = stringResource(R.string.random_pick)
                                            )
                                        }
                                        if (homeUiState.isFilterActive) {
                                            FilledTonalIconButton(onClick = { showBottomSheet = true }) {
                                                Icon(
                                                    Icons.Default.FilterList,
                                                    contentDescription = stringResource(R.string.filter_active)
                                                )
                                            }
                                        } else {
                                            IconButton(onClick = { showBottomSheet = true }) {
                                                Icon(
                                                    Icons.Default.FilterList,
                                                    contentDescription = stringResource(R.string.filter_inactive)
                                                )
                                            }
                                        }
                                    }
                                )
                                HomeScreen(
                                    itemList = homeUiState.itemList,
                                    isFilterActive = homeUiState.isFilterActive,
                                    onItemClick = { itemId ->
                                        scope.launch {
                                            listDetailPaneNavigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                DetailPaneState.ViewItem(itemId)
                                            )
                                        }
                                    }
                                )
                            }
                            FloatingActionButton(
                                onClick = { navController.navigate(ItemEntryDestination.route) },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .padding(
                                        end = WindowInsets.safeDrawing
                                            .asPaddingValues()
                                            .calculateEndPadding(LocalLayoutDirection.current),
                                        bottom = WindowInsets.safeDrawing
                                            .asPaddingValues()
                                            .calculateBottomPadding()
                                    )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.item_entry_title))
                            }
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

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    FilterSheetContent(
                        uiState = homeUiState,
                        onGenreSelected = viewModel::updateGenreFilter,
                        onPlayerCountChanged = viewModel::updatePlayerCountFilter,
                        onClearFilters = viewModel::clearFilters
                    )
                }
            }

            homeUiState.randomlySelectedItem?.let { item ->
                RandomItemDialog(
                    item = item,
                    onDismiss = { viewModel.clearRandomItem() }
                )
            }
            
            val isDetailPaneShowingContent = listDetailPaneNavigator.currentDestination?.contentKey.let { state ->
                state is DetailPaneState.ViewItem || state is DetailPaneState.EditItem
            }
            BackHandler(enabled = isDetailPaneShowingContent) {
                scope.launch {
                    when (val currentKey = listDetailPaneNavigator.currentDestination?.contentKey) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomItemDialog(item: Item, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = item.name, style = MaterialTheme.typography.headlineSmall) },
        text = {
            ItemDetails(item = item)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheetContent(
    uiState: HomeUiState,
    onGenreSelected: (String) -> Unit,
    onPlayerCountChanged: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Filter by", style = MaterialTheme.typography.titleLarge)

        if (uiState.allGenres.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Genre", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.allGenres.forEach { genre ->
                        InputChip(
                            selected = genre == uiState.selectedGenre,
                            onClick = { onGenreSelected(genre) },
                            label = { Text(genre) }
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Player Count", style = MaterialTheme.typography.titleMedium)
            TextField(
                value = uiState.playerCountFilter,
                onValueChange = onPlayerCountChanged,
                label = { Text("Enter number of players") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = onClearFilters,
            enabled = uiState.isFilterActive,
        ) {
            Text("Clear Filters")
        }
    }
}
