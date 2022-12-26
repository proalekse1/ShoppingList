package com.proalekse1.shoppinglist.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() { //базовый фрагмент
    abstract fun onClickNew() //абстрактная функция
}