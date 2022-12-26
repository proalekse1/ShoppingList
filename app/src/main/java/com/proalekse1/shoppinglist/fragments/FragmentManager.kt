package com.proalekse1.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import com.proalekse1.shoppinglist.R

object FragmentManager {
    var currentFrag: BaseFragment? = null //текущий фрагмент изначально нал его еще не открыли

    fun setFragment(newFrag: BaseFragment, activity: AppCompatActivity){ //функция переключения между фрагментами
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFrag) //указали куда помещать новый фрагмент
        transaction.commit() //применили изменения
        currentFrag = newFrag //новый фрагмент равен текущему
    }
}