package de.gabriel.listrandomizer.data

import java.io.File

data class Item(
    val id: Int,
    val name: String,
    val image: File? = null,
    val description: String? = null,
    val genre: String? = null,
    val minPlayer: Int? = null,
    val maxPlayer: Int? = null
){
    fun toItemEntry(): ItemEntry {
        return ItemEntry(
            id = id,
            name = name,
            imageName = image?.name ?: "",
            description = description,
            genre = genre,
            minPlayer = minPlayer,
            maxPlayer = maxPlayer
        )
    }

    companion object {
        fun fromItemEntry(itemEntry: ItemEntry, folderPath:File?): Item {
            return Item(
                id = itemEntry.id,
                name = itemEntry.name,
                image = if (itemEntry.imageName.isNotEmpty()) File(folderPath, itemEntry.imageName) else null,
                description = itemEntry.description,
                genre = itemEntry.genre,
                minPlayer = itemEntry.minPlayer,
                maxPlayer = itemEntry.maxPlayer
            )
        }
    }
}