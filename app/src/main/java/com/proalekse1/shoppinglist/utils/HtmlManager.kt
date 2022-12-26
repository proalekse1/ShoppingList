package com.proalekse1.shoppinglist.utils

import android.text.Html
import android.text.Spanned


object HtmlManager {
    fun getFromHtml(text: String): Spanned { // текст из базы данных превращается в HTML
        return if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(text)
        } else {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    fun toHtml(text: Spanned): String{ //HTML превращает в раскрашенный текст и передаем потом в базу данных
        return if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) {
            Html.toHtml(text)
        } else {
            Html.toHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}