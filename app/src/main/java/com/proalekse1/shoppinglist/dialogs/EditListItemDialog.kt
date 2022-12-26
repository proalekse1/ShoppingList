package com.proalekse1.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.EditListItemDialogBinding
import com.proalekse1.shoppinglist.databinding.NewListDialogBinding
import com.proalekse1.shoppinglist.entities.ShopListItem

object EditListItemDialog {
    fun showDialog(context: Context, item: ShopListItem, listener: Listener){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context)) //добираемся до элементов разметки диалога
        builder.setView(binding.root) //добавляем в буилдер нашу разметку
        binding.apply {//присваиваем слушатель нажатий на кнопку в диалоге
            edName.setText(item.name) // передаем в диалог старое название
            edInfo.setText(item.itemInfo) // передаем в диалог старое название
            if(item.itemType == 1) edInfo.visibility = View.GONE //прячем инфо если редактируем подсказку
            bUpdate.setOnClickListener{ //жмем кнопку обновить
                    if (edName.text.toString().isNotEmpty()){ //если едит текст не пуст записываем

                        listener.onClick(item.copy(name = edName.text.toString(), itemInfo = edInfo.text.toString())) //копируем новый текст в имя сущности
                    }
                    dialog?.dismiss() //закрываем диалог
                }
            }
        dialog = builder.create() //создаем диалог с помощью его буилдера
        dialog.window?.setBackgroundDrawable(null) //чтобы не вышел стандартный фон диалога передаем нал
        dialog.show() // показываем диалог
    }
    interface Listener{ // чтобы запустить создание списка надо добавить интерфейс со слушателем нажатий
        fun onClick(item: ShopListItem) //он передаст нажатие на майн активити, а передаст item новое название списка
    }
}