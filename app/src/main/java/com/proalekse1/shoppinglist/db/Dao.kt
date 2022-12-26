package com.proalekse1.shoppinglist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.proalekse1.shoppinglist.entities.LibraryItem
import com.proalekse1.shoppinglist.entities.NoteItem
import com.proalekse1.shoppinglist.entities.ShopListNameItem
import com.proalekse1.shoppinglist.entities.ShopListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>> // функция берет все заметки из базы данных, flow - это корутина
    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>> // функция берет все заметки из базы данных, flow - это корутина

    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>> // берет все продукты по идентификатору списка
    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem> // для посказок для продуктов берет все элементы из сущности library

    @Query("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int) // функция для удаления элементов списка записей из базы данных
    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int) // функция для удаления элементов списка имен покупок из базы данных
    @Query("DELETE FROM shop_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopItemsByListId(listId: Int) // функция удаления списка и продуктов из списка
    @Query("DELETE FROM library WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int) // функция для удаления элементов списка подсказок из базы данных

    @Insert
    suspend fun insertNote(note: NoteItem) // функция записи в сущность для заметок NoteItem, suspend второстепенный поток
    @Insert
    suspend fun insertItem(shopListItem: ShopListItem) // записывает новые элементы списка покупок
    @Insert
    suspend fun insertShopListName(nameItem: ShopListNameItem) // функция записи в сущность для заметок SoppingListName, suspend второстепенный поток
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem) // записывает новые продукты в сущность LibraryItem

    @Update
    suspend fun updateNote(note: NoteItem) // функция обновления записи
    @Update
    suspend fun updateLibraryItem(item: LibraryItem) // функция обновления подсказки
    @Update
    suspend fun updateListName(shopListNameItem: ShopListNameItem) // функция обновления записи
    @Update
    suspend fun updateListItem(item: ShopListItem) // функция обновления состояния чекбокса
}