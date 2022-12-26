package com.proalekse1.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context: Context, listener: Listener, name: String){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context)) //добираемся до элементов разметки диалога
        builder.setView(binding.root) //добавляем в буилдер нашу разметку
        binding.apply {//присваиваем слушатель нажатий на кнопку в диалоге
            edNewListName.setText(name)
            if(name.isNotEmpty()) bCreate.text = context.getString(R.string.update) //если список редактируется то взять название редактировать
            bCreate.setOnClickListener {
                val listName = edNewListName.text.toString()
                if(listName.isNotEmpty()){ //если название не пустое то работает слушатель нажатий
                    listener.onClick(listName)
                }
                dialog?.dismiss() //если пустое то просто закрываем диалог
            }
        }
        dialog = builder.create() //создаем диалог с помощью его буилдера
        dialog.window?.setBackgroundDrawable(null) //чтобы не вышел стандартный фон диалога передаем нал
        dialog.show() // показываем диалог
    }
    interface Listener{ // чтобы запустить создание списка надо добавить интерфейс со слушателем нажатий
        fun onClick(name: String) //он передаст нажатие на майн активити, а передаст name название списка
    }
}