package com.proalekse1.shoppinglist.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.billing.BillingManager

class SettingsFragment : PreferenceFragmentCompat() { //фрагмент для настроек
    private lateinit var removeAdsPref: Preference //переменная для настроек
    private lateinit var bManager: BillingManager //переменная для билинг менеджер

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey) //передаем разметку и ключ
        init()
    }

    private fun init(){ //инициализироали настройки
        bManager = BillingManager(activity as AppCompatActivity) //инициализировали билинг менеджер
        removeAdsPref = findPreference("remove_ads_key")!! //точно не равно нал
        removeAdsPref.setOnPreferenceClickListener { //слушатель нажатий
            //Log.d("MyLog", "On Remove ads pressed") //проверяем слушатель

            bManager.startConnection() //запускаем коннект с гугл плей
            true
        }
    }

    override fun onDestroy() { //закрывается коннект с плеймаркетом
        bManager.closeConnection()
        super.onDestroy()
    }
}