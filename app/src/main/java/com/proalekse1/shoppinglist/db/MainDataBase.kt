package com.proalekse1.shoppinglist.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.proalekse1.shoppinglist.entities.LibraryItem
import com.proalekse1.shoppinglist.entities.NoteItem
import com.proalekse1.shoppinglist.entities.ShopListItem
import com.proalekse1.shoppinglist.entities.ShopListNameItem

//указали что это создание базы данных и передаем массив из всех сущностей
@Database (entities = [LibraryItem::class, NoteItem::class,
    ShopListItem::class, ShopListNameItem::class], version = 1,
        exportSchema = true//, autoMigrations = [
        // AutoMigration(from = 1, to = 2, spec = MainDataBase.SpecMigration::class)
        // ] //для автомиграции
    )
abstract class MainDataBase : RoomDatabase() { //абстрактный класс

    @DeleteColumn (tableName = "library", columnName = "price")//аннотация для миграции базы при удалении
    class SpecMigration : AutoMigrationSpec //для миграции базы при удалении

    abstract fun getDao(): Dao //инициализируем интерфейс Dao

    companion object{ //функция доступная без инициализации
        @Volatile //для работы с переменной несколькими потоками
        private var INSTANCE: MainDataBase? = null //переменная для запроса базы данных
        fun getDataBase(context: Context): MainDataBase{ //функция которая фозвращает MainDataBase
            return INSTANCE ?: synchronized(this){ //элвис оператор нужен - если база еще не создана null он запустит когд справа
                val instance = Room.databaseBuilder( // создание базы данных
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db" // имя базы данных
                ).build()
                instance
            }
        }
    }
}