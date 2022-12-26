package com.proalekse1.shoppinglist.activities

import android.app.Application
import com.proalekse1.shoppinglist.db.MainDataBase

class MainApp : Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}