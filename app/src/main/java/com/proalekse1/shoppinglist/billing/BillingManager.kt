package com.proalekse1.shoppinglist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

class BillingManager(val activity: AppCompatActivity) { //класс для покупок
    private var bClient: BillingClient? = null

    init { //инициализируем билинг клиент
       setUpBillingClient()
    }

    private fun setUpBillingClient(){ //этой функцией подключаемся к плей маркету и выходит диалог в приложении где можно купит
        bClient = BillingClient.newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases()
            .build()
    }

    private fun savePref(isPurchase: Boolean){//записывае отсутствие рекламы
        val pref = activity.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_KEY, isPurchase)
        editor.apply()
    }


    fun startConnection(){ //функция запуска всего диалога и покупки и всех остальных в этом классе
        bClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() { //если произошла ошибка

            }

            override fun onBillingSetupFinished(p0: BillingResult) { //если прошло все успешно запускаем диалог
                getItem()
            }

        })
    }

    private fun getItem(){ //получить покупку после того как нажали купить
        val skuList = ArrayList<String>() //создаем пустой список покупок
        skuList.add(REMOVE_AD_ITEM) //получаем ключ после покупки
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP) //SkuType.INAPP тип покупки постоянная
        bClient?.querySkuDetailsAsync(skuDetails.build()){ //делам запрос диалога асинхронно чтобы не тормозить основной поток
            bResult, list -> //лямбда
            run { //run блок
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK){ //если коды совпадают то функция запустится
                    if (list != null){ //если список не нал
                        if (list.isNotEmpty()){ //если не пуст
                            val bFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(list[0]).build()
                            bClient?.launchBillingFlow(activity, bFlowParams) //запускаем диалог
                        }
                    }
                }
            }
        }
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener{ //листенер где проверяем код отправленный соответствует принятому
        return PurchasesUpdatedListener { //лямбда выражение
                bResult, list -> //код ресультат и список с пкупками
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) } //если список с покупками не пустой то запустится
                }
            }

        }
    }

    private fun nonConsumableItem(purchase: Purchase){ //подтверждение покупки
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){ //если статус покупки действительно куплена
            if (!purchase.isAcknowledged){ //если подтверждено
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build() //если подтверждено передаем токен
                bClient?.acknowledgePurchase(acParams){ //передаем в клиент токен с покупкой
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePref(true)//записали в настройки отсутствие рекламы
                        Toast.makeText(activity, "Спасибо за покупку!", Toast.LENGTH_LONG).show()//тостик
                    } else {
                        savePref(false)
                        Toast.makeText(activity, "Не удалось произвести покупку!", Toast.LENGTH_LONG).show()//тостик
                    }
                }
            }
        }
    }

    fun closeConnection(){
        bClient?.endConnection()//закрываем соединение с плеймаркет
    }

    companion object{ //нужна для плеймаркета в качестве ключа
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref" //константа чтобы убрать рекламу и записать настройку
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }

}