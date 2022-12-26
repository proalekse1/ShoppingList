package com.proalekse1.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.proalekse1.shoppinglist.databinding.DeleteDialogBinding
import com.proalekse1.shoppinglist.databinding.NewListDialogBinding

object DeleteDialog {
    fun showDialog(context: Context, listener: Listener){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context)) //добираемся до элементов разметки диалога
        builder.setView(binding.root) //добавляем в буилдер нашу разметку
        binding.apply { //присваиваем слушатель нажатий на кнопку в диалоге
            bDelete.setOnClickListener { //жмем удалить
                listener.onClick()
                dialog?.dismiss()
            }
            bCancel.setOnClickListener { //жмем отмена
                dialog?.dismiss()
            }
        }
        dialog = builder.create() //создаем диалог с помощью его буилдера
        dialog.window?.setBackgroundDrawable(null) //чтобы не вышел стандартный фон диалога передаем нал
        dialog.show() // показываем диалог
    }
    interface Listener{ // чтобы запустить создание списка надо добавить интерфейс со слушателем нажатий
        fun onClick() //он передаст нажатие на майн активити, а передаст name название списка
    }
}