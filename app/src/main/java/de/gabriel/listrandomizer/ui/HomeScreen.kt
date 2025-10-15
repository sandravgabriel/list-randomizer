package de.gabriel.listrandomizer.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.gabriel.listrandomizer.R
import de.gabriel.listrandomizer.data.Item
import de.gabriel.listrandomizer.data.ItemEntry
import de.gabriel.listrandomizer.ui.navigation.NavigationDestination
import de.gabriel.listrandomizer.ui.theme.ListRandomizerTheme

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    isFilterActive: Boolean,
    modifier: Modifier = Modifier
) {
    HomeBody(
        itemList = itemList,
        onItemClick = onItemClick,
        isFilterActive = isFilterActive,
        modifier = modifier,
    )
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    isFilterActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxSize()
    ) {
        if (itemList.isEmpty()) {
            val emptyText = if (isFilterActive) {
                stringResource(R.string.no_item_filtered_description)
            } else {
                stringResource(R.string.no_item_description)
            }
            Text(
                text = emptyText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            HomeList(
                itemList = itemList,
                onItemClick = { onItemClick(it.id) },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun HomeList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(items = itemList, key = { it.id }) { item ->
            HomeItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_extra_small))
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun HomeItem(
    item: Item, modifier: Modifier = Modifier
) {
    val userImageAvailable = item.image != null
    val painter = painterResource(id = R.drawable.default_image)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (userImageAvailable) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.image)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.error_24px)
                        .listener(onError = { request, result ->
                            Log.e("Coil", "Error loading image for ${request.data}: ${result.throwable}")
                        })
                        .build(),
                    contentDescription = "selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .aspectRatio(1f)
                )
            } else {
                Image(
                    painter = painter,
                    contentDescription = "default image",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                )
            }

            Spacer(Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    ListRandomizerTheme {
        HomeBody(
            itemList = listOf(
                Item.fromItemEntry(ItemEntry(1, "Item", "", description = "", genre = "", minPlayer = 1, maxPlayer = 6), null),
                Item.fromItemEntry(ItemEntry(2, "Item", "", description = "", genre = "", minPlayer = 1, maxPlayer = 6), null),
                Item.fromItemEntry(ItemEntry(3, "Item", "", description = "", genre = "", minPlayer = 1, maxPlayer = 6), null)
            ),
            onItemClick = {},
            isFilterActive = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    ListRandomizerTheme {
        HomeBody(listOf(), onItemClick = {}, isFilterActive = false)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyFilteredListPreview() {
    ListRandomizerTheme {
        HomeBody(listOf(), onItemClick = {}, isFilterActive = true)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeItemPreview() {
    ListRandomizerTheme {
        HomeItem(
            Item.fromItemEntry(ItemEntry(1, "Item", "", description = "", genre = "", minPlayer = 1, maxPlayer = 6), null)
        )
    }
}
