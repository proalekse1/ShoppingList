package com.proalekse1.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_list") // класс для заметок
data class NoteItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "title") //заголовок записи
    val title: String,
    @ColumnInfo(name = "content") //сам тект записи
    val content: String,
    @ColumnInfo(name = "time") // время заметки
    val time: String,
    @ColumnInfo(name = "category") // для фильтра
    val category: String

): Serializable //нужно чтобы отправлять целый класс
