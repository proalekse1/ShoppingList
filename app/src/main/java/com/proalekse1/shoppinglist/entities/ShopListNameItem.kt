package com.proalekse1.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "shopping_list_names") //имя таблицы
data class ShopListNameItem( //дата класс
    //Все с @ это ссылки на библиотеку room Persistant Libriry
    @PrimaryKey (autoGenerate = true) //автоматом заполняется 1я колонка, порядкоый номер
    val id: Int?, //переменная для номера продукта, может быть равно null

    @ColumnInfo (name = "name") //колонка для названия списка, в скобках название колонки
    val name: String, //переменная для колонки с названием?

    @ColumnInfo (name = "time") //колонка для времени
    val time: String, //переменная для колонки с временем

    @ColumnInfo (name = "allItemCounter") //колонка для общего количества покупок, нужен будет для прогресс бара
    val allItemCounter: Int, //переменная для колонки с общим количеством покупок

    @ColumnInfo (name = "checkedItemsCounter") //колонка для купленных покупок(checkbox)
    val checkedItemsCounter: Int, //переменная для колонки с купленными покупками

    @ColumnInfo (name = "itemsIds") //колонка для идентификаторов всех элементов
    val itemsIds: String //переменная для идентификаторов всех элементов
): Serializable
