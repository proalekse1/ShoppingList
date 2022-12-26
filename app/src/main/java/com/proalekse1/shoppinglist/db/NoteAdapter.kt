package com.proalekse1.shoppinglist.db

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.NoteListItemBinding
import com.proalekse1.shoppinglist.entities.NoteItem
import com.proalekse1.shoppinglist.utils.HtmlManager
import com.proalekse1.shoppinglist.utils.TimeManager

class NoteAdapter(private val listener: Listener,private val defPref: SharedPreferences) : ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemComporator()) { //будет создавать 1 элемент списка в треугольных скобках база NoteItem и


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //создание холдера
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //заполняем холдер
        holder.setData(getItem(position), listener, defPref) // listener интерфейс кнопки удалить на каждом эелементе
    }


    class ItemHolder(view: View) : RecyclerView.ViewHolder(view){ // в этом класссе и будем заполнять элемент
        private val binding = NoteListItemBinding.bind(view) //подключаем к разметке байндинг

        fun setData(note: NoteItem, listener: Listener, defPref: SharedPreferences) = with(binding){
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time, defPref)
            itemView.setOnClickListener { //cлушатель нажатий на весь элемент
                listener.onClickItem(note)
            }
            imDelete.setOnClickListener{ //слушатель нажатий для кнопки удалить элемент
                listener.deleteItem(note.id!!) // !! точно не равен null, id это номер в базе данных
            }
        }
        companion object{
            fun create(parent: ViewGroup): ItemHolder{
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.note_list_item, parent, false))
            }
        }
    }
    class ItemComporator : DiffUtil.ItemCallback<NoteItem>(){ //дифутил сравнивает элементы если находит новые добавляет в список
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean { //сравнивает равны ли элементы
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean { //сравнивает весь контент
            return oldItem == newItem
        }

    }

    interface Listener{ //интерфейс для кнопки удалить на заметке
        fun deleteItem(id: Int) //функция удаления
        fun onClickItem(note: NoteItem) //функция изменения по нажатию на элемент всей заметки
    }

}