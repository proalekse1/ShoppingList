package com.proalekse1.shoppinglist.db

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.ShopLibraryListItemBinding
import com.proalekse1.shoppinglist.databinding.ShopListItemBinding
import com.proalekse1.shoppinglist.entities.ShopListItem

class ShopListItemAdapter(private val listener: Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComporator()) { //будет создавать 1 элемент списка в треугольных скобках база NoteItem и


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //создание холдера
        return if(viewType == 0) //проверка какую разметку показать
                ItemHolder.createShopItem(parent)
                else
                    ItemHolder.createLibraryItem(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //заполняем холдер
        if(getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener) // если 0 заполняем так холдер
        } else {
            holder.setLibraryData(getItem(position), listener) // если 1 заполняем холдер разметкой с подсказками
        }
    }

    override fun getItemViewType(position: Int): Int { //показываем по позиции какую разметку покажем
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view){ // в этом класссе и будем заполнять элемент

        fun setItemData(shopListItem: ShopListItem, listener: Listener){ // показывает и заполняет один элемент
            val binding = ShopListItemBinding.bind(view) //подключаем к разметке байндинг
            binding.apply{
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem) //делаем элемент видимым или нет
                chBox.isChecked = shopListItem.itemChecked //берет из базы данных чекбокс отмечен или нет
                setPaintFlagAndColor(binding) //зачеркиает если чекбокс нажат
                chBox.setOnClickListener{ //слушатель нажатий для чекбокса
                    listener.onClickItem(shopListItem.copy(itemChecked = chBox.isChecked), CHECK_BOX)//сохраняет состояние чекбокса
                }
                imEdit.setOnClickListener{ //передаем на кнопку карандаш листенер
                    listener.onClickItem(shopListItem, EDIT)
                }
            }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener){ // показывает один элемент для подсказки
            val binding = ShopLibraryListItemBinding.bind(view) //подключаем к разметке для подсказок байндинг
            binding.apply {
                tvName.text = shopListItem.name
                imEdit.setOnClickListener{ //передаем на кнопку карандаш листенер
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imDelete.setOnClickListener{ //передаем на кнопку удалить листенер
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener { //нажатие на весь элемент
                    listener.onClickItem(shopListItem, ADD)
                }
            }
        }
        private fun setPaintFlagAndColor(binding: ShopListItemBinding) { //выделение чекбокса и текста
            binding.apply {
            if(chBox.isChecked){ //если чекбокс отмечен
                tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //перечеркиваем текст
                tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_light)) //изменяем цвет текста
                tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_light)) //изменяем цвет текста
            } else {
                tvName.paintFlags = Paint.ANTI_ALIAS_FLAG //убираем перечеркивание текста
                tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) //изменяем цвет текста
                tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }
          }
        }
        private fun infoVisibility(shopListItem: ShopListItem): Int{
            return if(shopListItem.itemInfo.isEmpty()){ //если пусто и налл делаем чтобы элемент прятался
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object{
            fun createShopItem(parent: ViewGroup): ItemHolder{
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.shop_list_item, parent, false)) //надуваем разметку
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder{
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.shop_library_list_item, parent, false)) //надуваем разметку для подсказок
            }
        }
    }
    class ItemComporator : DiffUtil.ItemCallback<ShopListItem>(){ //дифутил сравнивает элементы если находит новые добавляет в список
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean { //сравнивает равны ли элементы
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean { //сравнивает весь контент
            return oldItem == newItem
        }

    }

    interface Listener{ //интерфейс для кнопки удалить на заметке
        fun onClickItem(shopListItem: ShopListItem, state: Int) //функция изменения по нажатию на элемент всей заметки
    }

    companion object{ //для листенера
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2 //для редактирования посказок
        const val DELETE_LIBRARY_ITEM = 3 //для удаления посказок
        const val ADD = 4 //для нажатия на подсказку
    }

}