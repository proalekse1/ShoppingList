package com.proalekse1.shoppinglist.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    const val DEF_TIME_FORMAT = "hh:mm:ss - yyyy/MM/dd"
     fun getCurrentTime(): String{ // функция времени
        val formatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault()) //формат времени
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, defPreferences: SharedPreferences): String{//достаем формат из экрана с настройками
        val defFormatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val defDate = defFormatter.parse(time) //parse это формат собранный в одну строчку мы разбиваем на элементы
        val newFormat = defPreferences.getString("time_format_key", DEF_TIME_FORMAT) //собираем дату в формат выбранный в настройках
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())
        return if(defDate != null){
            newFormatter.format(defDate)
        } else {
            time
        }
    }

}