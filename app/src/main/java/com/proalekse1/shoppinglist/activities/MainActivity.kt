package com.proalekse1.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.billing.BillingManager
import com.proalekse1.shoppinglist.databinding.ActivityMainBinding
import com.proalekse1.shoppinglist.dialogs.NewListDialog
import com.proalekse1.shoppinglist.fragments.FragmentManager
import com.proalekse1.shoppinglist.fragments.NoteFragment
import com.proalekse1.shoppinglist.fragments.ShopListNamesFragment
import com.proalekse1.shoppinglist.settings.SettingsActivity


class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences //доступ для настроек по умолчанию
    private var currentMenuItemid = R.id.shop_list //для обновления вида после изменения настроек
    private var currentTheme = "" //переменная для смены темы
    private var iAd: InterstitialAd? = null //переменная для рекламы
    private var adShowCounter = 0 //счетчик для нажатий на иконки - это для рекламы
    private var adShowCounterMax = 3 //счетчик для нажатий на иконки - это для рекламы, максимальное значение
    private lateinit var pref: SharedPreferences //настройки для рекламы

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)//для настроек
        currentTheme = defPref.getString("theme_key","blue").toString() //для смены темы
        setTheme(getSelectedTheme()) //изменение темы
        
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE) //отключение рекламы
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        setBottomNavListener()
        if(!pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false))loadInterAd() //для запуска рекламы
    }

    private fun loadInterAd(){ // функция для загрузки рекламы
        val request = AdRequest.Builder().build() //запрос для получения рекламы
        //загружаем и предаем контекст, id рекламы, передаем колбэк если загрузилось или не загрузилось
        InterstitialAd.load(this, getString(R.string.inter_ad_id), request,
            object : InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) { //если реклама загрузилась успешно
                    iAd = ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    iAd = null //если реклама загрузилась не успешно
                }
        })
    }

    private fun showInterAd(adListener: AdListener){ //функция для показа рекламы
        if(iAd != null && adShowCounter > adShowCounterMax && !pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false)){ //если реклама загрузилась && adShowCounter > adShowCounterMax
            //колбек просто следит за тем то происходит с объявлением
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() { //нажал крестик закрыть объявление
                    iAd = null //делаем null наше объявление потому что его уже посмотрели
                    loadInterAd() //загрузим следующее объявление
                    adListener.onFinish() //если пользователь нажал крестик закрываем объявление
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) { //если произошла ошибка в показе рекламы
                    iAd = null //делаем null наше объявление потому что его уже посмотрели
                    loadInterAd() //загрузим следующее объявление
                }

                override fun onAdShowedFullScreenContent() { //запускается когда полностью реклама была показана
                    iAd = null //делаем null наше объявление потому что его уже посмотрели
                    loadInterAd() //загрузим следующее объявление
                }
            }
            adShowCounter = 0 //обнуляем счетчик
            //запускаем показ
            iAd?.show(this)
        } else { //если объявление не было загружено
            adShowCounter++ //увеличиваем счетчик если реклаа не показывалась
            adListener.onFinish()
        }
    }

    private fun setBottomNavListener(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings->{
                    showInterAd(object : AdListener{ //передаем функцию показа рекламы, по нажатию на кнопку настройки
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) //запускаем активити с настройками
                        }

                    })

                }
                R.id.notes->{ //открываем заметки
                    showInterAd(object : AdListener{ //передаем функцию показа рекламы, по нажатию на кнопку настройки
                        override fun onFinish() {
                            currentMenuItemid = R.id.notes
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity)
                        }

                    })

                }
                R.id.shop_list->{
                    currentMenuItemid = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.new_item->{
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }

    override fun onResume(){ //при возврате из настроек нажатой будет кнопка которая была
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemid
        if(defPref.getString("theme_key","blue") != currentTheme) recreate() //проверка для смены темы
    }

    private fun getSelectedTheme(): Int{ //считываем настройки темы
        return if(defPref.getString("theme_key", "blue") == "blue"){ //ключ и значение по умолчанию
            R.style.Theme_ShoppingListBlue
        } else {
            R.style.Theme_ShoppingListRed
        }
    }

    override fun onClick(name: String) {
        Log.d("MyLog", "Name: $name")
    }

    interface AdListener{ //интерфейс для рекламы
        fun onFinish()
    }
}






