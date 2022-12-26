package com.proalekse1.shoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.proalekse1.shoppinglist.activities.MainApp
import com.proalekse1.shoppinglist.activities.ShopListActivity
import com.proalekse1.shoppinglist.databinding.FragmentShopListNamesBinding
import com.proalekse1.shoppinglist.db.MainViewModel
import com.proalekse1.shoppinglist.db.ShopListNameAdapter
import com.proalekse1.shoppinglist.dialogs.DeleteDialog
import com.proalekse1.shoppinglist.dialogs.NewListDialog
import com.proalekse1.shoppinglist.entities.ShopListNameItem
import com.proalekse1.shoppinglist.utils.TimeManager


class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener{ //
    private lateinit var binding: FragmentShopListNamesBinding // подключаем байндинг
    private lateinit var adapter: ShopListNameAdapter //подключаем адаптер

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{ // запускаем диалог
            override fun onClick(name: String) {
                val shopListName = ShopListNameItem( //передаем по нажатию на кнопку создать имя время и прочие переменные сущности ShoppingListName
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName) //добавили в базу
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //запускаем ресайклер вью
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer() // запускаем обсервер который будет слушать изменения в базе данных
    }

    private fun initRcView() = with(binding){ //инициализируем адаптер и ресайклервью
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }

    private fun observer(){   //слушатель изменений в базе данных
        mainViewModel.allShopListNamesItem.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id, true) //удаляем элементы во фрагменте
            }

        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{ // запускаем диалог
            override fun onClick(name: String) {

                mainViewModel.updateListName(shopListNameItem.copy(name = name)) //копируем новое название в старое
            }
        }, shopListNameItem.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) { //при нажатии на весь элемент откроется активити
        val i = Intent(activity, ShopListActivity::class.java).apply{ //в скобках куда передаем
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem) //кладем сюда сущность и передаем в активити
        }
        startActivity(i)
    }
}