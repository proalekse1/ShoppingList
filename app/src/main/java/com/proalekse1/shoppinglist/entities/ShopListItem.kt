package com.proalekse1.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_list_item") // название таблицы
data class ShopListItem(

    @PrimaryKey(autoGenerate = true) //id каждого элемента списка
    val id: Int?,
    @ColumnInfo(name = "name") //название продукта
    val name: String,
    @ColumnInfo(name = "itemInfo") //дополнительная информация
    val itemInfo: String = "",
    @ColumnInfo(name = "itemChecked") // для чекбокса, купил отметил
    val itemChecked: Boolean = false,
    @ColumnInfo(name = "listId") //id списка
    val listId: Int,
    @ColumnInfo(name = "itemType") // понадобится для быстрого заполнения(подсказки того что уже вводили)
    val itemType: Int = 0

)
