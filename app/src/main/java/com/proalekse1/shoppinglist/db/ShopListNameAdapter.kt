package com.proalekse1.shoppinglist.db

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.ListNameItemBinding
import com.proalekse1.shoppinglist.entities.ShopListNameItem

class ShopListNameAdapter(private val listener: Listener) : ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComporator()) { //будет создавать 1 элемент списка в треугольных скобках база NoteItem и


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //создание холдера
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //заполняем холдер
        holder.setData(getItem(position), listener) // listener интерфейс кнопки удалить на каждом эелементе
    }


    class ItemHolder(view: View) : RecyclerView.ViewHolder(view){ // в этом класссе и будем заполнять элемент
        private val binding = ListNameItemBinding.bind(view) //подключаем к разметке байндинг

        fun setData(shopListNameItem: ShopListNameItem, listener: Listener) = with(binding){ // показывает один элемент
            tvListName.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time

            pBar.max = shopListNameItem.allItemCounter //максимальное количество для прогресс бара
            pBar.progress = shopListNameItem.checkedItemsCounter //сколько элементов отмечено, для прогресс бара
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem, binding.root.context)) //передаем цвет прогрессбару
            pBar.progressTintList = colorState
            counterCard.backgroundTintList = colorState //меняем цвет элементу счетчика

            val counterText = "${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCounter}" //счетчик элементов
            tvCounter.text = counterText

            itemView.setOnClickListener { //cлушатель нажатий на весь элемент
                listener.onClickItem(shopListNameItem)
            }
            imDelete.setOnClickListener{ //слушатель нажатий для кнопки удалить элемент
                listener.deleteItem(shopListNameItem.id!!) //подключаем листенер к кнопке удалить
            }
            imEdit.setOnClickListener{ //слушатель нажатий для кнопки редактировать элемент
                listener.editItem(shopListNameItem) //подключаем листенер к кнопке редактировать
            }
        }

        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int{ //смена цвета прогресбара
                                                    //контекст нужен чтобы добраться до цветов
            return if(item.checkedItemsCounter == item.allItemCounter){ //если все продукты купили то зеленый
                ContextCompat.getColor(context, R.color.green_main)
            } else {                                                    //если не все продукты купили то красный
                ContextCompat.getColor(context, R.color.red_main)
            }
        }


        companion object{
            fun create(parent: ViewGroup): ItemHolder{
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.list_name_item, parent, false))
            }
        }
    }
    class ItemComporator : DiffUtil.ItemCallback<ShopListNameItem>(){ //дифутил сравнивает элементы если находит новые добавляет в список
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean { //сравнивает равны ли элементы
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean { //сравнивает весь контент
            return oldItem == newItem
        }

    }

    interface Listener{ //интерфейс для кнопки удалить на заметке
        fun deleteItem(id: Int) //функция удаления
        fun editItem(shopListNameItem: ShopListNameItem) //функция редактирования
        fun onClickItem(shopListNameItem: ShopListNameItem) //функция изменения по нажатию на элемент всей заметки
    }

}