package com.proalekse1.shoppinglist.utils

import android.content.Intent
import com.proalekse1.shoppinglist.entities.ShopListItem

object ShareHelper {
    //будем передаать с помощью интента списпок с продуктами и название списка
    fun shareShopList(shopList: List<ShopListItem>, listName: String): Intent{ //возвращает интент
        val intent = Intent(Intent.ACTION_SEND) //указываем что что-то отправляем
        intent.type = "text/plane" //указываем тип данных который хотим отправить
        intent.apply{
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName)) //положить в интент
        }
        return intent
    }

    //тут будет формироваться в нужном виде список для передачи
    private fun makeShareText(shopList: List<ShopListItem>, listName: String): String{ //возвращает стринг
        val sBuilder = StringBuilder()
        sBuilder.append("<<$listName>>") //добавить заголовок
        sBuilder.append("\n") //добавить разрыв
        var counter = 0
        shopList.forEach { //прочитываем весь список
            sBuilder.append("${++counter} - ${it.name} (${it.itemInfo})") //добавляем строку 1+имя+доп.информация
            sBuilder.append("\n") //добавляем переход на новую строку
        }
        return sBuilder.toString() //возвращаем стринг
    }
}