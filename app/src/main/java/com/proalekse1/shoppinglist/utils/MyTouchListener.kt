package com.proalekse1.shoppinglist.utils

import android.view.MotionEvent
import android.view.View

class MyTouchListener : View.OnTouchListener { // класс для передвижения color picker
    var xDelta = 0.0f
    var yDelta = 0.0f
    override fun onTouch(v: View, event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> { //закрепляет когда отпускаем
                xDelta = v.x - event.rawX // позиция на которой был - позици на которой сейчас
                yDelta = v.y - event.rawY // позиция на которой был - позици на которой сейчас
            }
            MotionEvent.ACTION_MOVE -> { //перемещает
                v.x = xDelta + event.rawX
                v.y = yDelta + event.rawY
            }
        }
        return true
    }
}