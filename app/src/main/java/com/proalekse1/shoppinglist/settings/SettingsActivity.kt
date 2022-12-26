package com.proalekse1.shoppinglist.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.proalekse1.shoppinglist.R

class SettingsActivity : AppCompatActivity() { //активити для настроек 51 урок
    private lateinit var defPref: SharedPreferences // для настроек

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)//доступ для настроек
        setTheme(getSelectedTheme()) //изменение темы
        setContentView(R.layout.activity_settings)

        if(savedInstanceState == null){ //если фрагмент настроек не запущен, то запустить
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.placeHolder, SettingsFragment()).commit() //запустили фрагмент
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //стрелка назад в акшин баре
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish() //слушатель кнпки назад
        return super.onOptionsItemSelected(item)
    }

    private fun getSelectedTheme(): Int{ //считываем настройки темы
        return if(defPref.getString("theme_key", "blue") == "blue"){ //ключ и значение по умолчанию
            R.style.Theme_ShoppingListBlue
        } else {
            R.style.Theme_ShoppingListRed
        }
    }
}