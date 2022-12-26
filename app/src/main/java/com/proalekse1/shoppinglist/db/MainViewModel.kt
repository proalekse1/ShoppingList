package com.proalekse1.shoppinglist.db

import androidx.lifecycle.*
import com.proalekse1.shoppinglist.entities.LibraryItem
import com.proalekse1.shoppinglist.entities.NoteItem
import com.proalekse1.shoppinglist.entities.ShopListItem
import com.proalekse1.shoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.launch


class MainViewModel(database: MainDataBase) : ViewModel() { // Промежуточный класс в mvvm
    val dao = database.getDao() //инициализировали базу данных
    val libraryItems = MutableLiveData<List<LibraryItem>>() //инициализируем лайв дата для посказок продуктов
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData() //слушаем изменения в наших заметках и в случае обнаружения обновляем адаптер
    val allShopListNamesItem: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData() //слушаем изменения списка названий покупок

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{ // получаем список продуктов(лайв дата для обсервера)
        return dao.getAllShopListItems(listId).asLiveData()
    }
    // следит и получает для передачи в активити продукты для подсказок
    fun getAllLibraryItems(name: String) = viewModelScope.launch{ // = viewModelScope.launch на второстепенном потоке
        libraryItems.postValue(dao.getAllLibraryItems(name)) //postValue передаем значение на обсервер в активити
    }

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShopListName(listNameItem: ShopListNameItem) = viewModelScope.launch { //добавляет в базу название нового списка
        dao.insertShopListName(listNameItem)
    }
    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch { //добавляет в базу название нового списка
        dao.insertItem(shopListItem)
        //если в библиотеке нет такого продукта то добавить
        if(!isLibraryItemExists(shopListItem.name)) dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
    }

    fun updateListItem(item: ShopListItem) = viewModelScope.launch { //сохранение состояния чекбокса
        dao.updateListItem(item)
    }
    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }
    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch { //редактирование подсказок
        dao.updateLibraryItem(item)
    }
    fun updateListName(shopListNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.updateListName(shopListNameItem)
    }

    fun deleteNote(id: Int) = viewModelScope.launch { //viewModelScope корутина
        dao.deleteNote(id)
    }
    fun deleteLibraryItem(id: Int) = viewModelScope.launch { //удаление подсказки
        dao.deleteLibraryItem(id)
    }
    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch { //удаление списка с названием и продуктами в нем
        if(deleteList)dao.deleteShopListName(id)
        dao.deleteShopItemsByListId(id)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty() //если в списке нет такого продукта выдаст false если есть true
    }
    

    class MainViewModelFactory(val database: MainDataBase) : ViewModelProvider.Factory{ //класс который инициаизирует класс MainViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }

    }
}