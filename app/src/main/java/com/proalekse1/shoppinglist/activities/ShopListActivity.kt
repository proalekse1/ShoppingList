package com.proalekse1.shoppinglist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.ActivityShopListBinding
import com.proalekse1.shoppinglist.db.MainViewModel
import com.proalekse1.shoppinglist.db.ShopListItemAdapter
import com.proalekse1.shoppinglist.dialogs.EditListItemDialog
import com.proalekse1.shoppinglist.entities.LibraryItem
import com.proalekse1.shoppinglist.entities.ShopListItem
import com.proalekse1.shoppinglist.entities.ShopListNameItem
import com.proalekse1.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding //подключаем байндинг
    private var shopListNameItem: ShopListNameItem? = null //инициализируем сущность
    private lateinit var saveItem: MenuItem // инициализируем кнопку сохранить
    private var edItem: EditText? = null //делаем переменную для editeText
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher //переменная для подсказок

    private val mainViewModel: MainViewModel by viewModels { //инициализируем мейн вью модел
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()//запускаем ресайклер вью
        listItemObserver() //обсервер который слушает изменения в базе данных
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //подключаем меню с кнопками
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!! //находим кнопку сохранить
        val newItem = menu.findItem(R.id.new_item)!! //находим кнопку new
        edItem = newItem.actionView.findViewById(R.id.edNewShopItem) as EditText //находим editText
        newItem.setOnActionExpandListener(expandActionView()) //назначаем листенер
        saveItem.isVisible = false //делаем кнопку сохранить невидимой
        textWatcher = textWatcher() //слушатель для посказок
        return true
    }

    private fun textWatcher(): TextWatcher{ //функция для подсказок
        return object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { // слушатель букв для посказок
                Log.d("MyLog", "On Text Changed: $s")
                mainViewModel.getAllLibraryItems("%$s%") //%s% чтобы слушал по буквам
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //слушатель для кнопки сохранить
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(edItem?.text.toString()) //срабатывает функция заполнения
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true) //удаление списка названий и продуктов
                finish() //закрываем активити
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false) //удаление продуктов
            }
            R.id.share_list -> {
                startActivity(Intent.createChooser(//Choser нужен для выбора установленных приложений
                    //лист берем из адаптера потомучто адаптер знает о существовании этой сущности adapter?.currentList!!; отсюда берем имя
                    ShareHelper.shareShopList(adapter?.currentList!!, shopListNameItem?.name!!), //делимся списком с помощью запуска интента
                    "Поделиться с помощью"
                    ))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String){ //заполняем сущность ShopListItem
        if(name.isEmpty())return
        val item = ShopListItem(
            null,
            name,
            "",
            false,
            shopListNameItem?.id!!,
            0
        )
        edItem?.setText("") //делаем пустой эдит текст после сохранения
        mainViewModel.insertShopItem(item) //и пердаем все в мейн вью модел
    }

    private fun listItemObserver(){ //обсервер который слушает изменения в базе данных
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this,{
            adapter?.submitList(it) //как только обновилось передаем в адаптер
            binding.tvEmpty.visibility = if(it.isEmpty()){ //если список с продуктами пустой показываем Список пуст
                View.VISIBLE
            } else { //если не пустой прячем "Список пуст"
                View.GONE
            }
        })
    }

    //обсервер который будет наблюдать как обновляются элементы библиотеки посказок продуктов
    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this, {
            val tempShopList = ArrayList<ShopListItem>() //создали пустой список
            it.forEach { item-> //перебираем и заполняем элементы одной сущности элементами другой
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem) //добавляем к пустому списку элементы из forEach
            }
            adapter?.submitList(tempShopList) //передаем новый список с подсказками в адаптер
            binding.tvEmpty.visibility = if(it.isEmpty()){ //если список с подсказками пустой показываем Список пуст
                View.VISIBLE
            } else { //если не пустой прячем "Список пуст"
                View.GONE
            }
        })
    }

    private fun initRcView() = with(binding){ //подключаем ресайклер вью
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter = adapter
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{ //слушатель для появления кнопки сохранить
        return object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                saveItem.isVisible = true // нажали плюсик кнопка сохранить появилась
                edItem?.addTextChangedListener(textWatcher) //подключаем вотчер для подсказок если уже вводили этот продукт
                libraryItemObserver() //слушатель для посказок
                //отключаем наш основной обсервер
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%") //когда эдит текст пустой показать все посказки, а потом фильтровать по буквам
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean { //тут меню закрывается
                saveItem.isVisible = false //кнопка спряталась
                edItem?.removeTextChangedListener(textWatcher) //убираем вотчер
                invalidateOptionsMenu() //чтобы меню перерисовалось
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity) //закрываем слушатель для подсказок
                edItem?.setText("") //очистит эдит текст от введенных букв
                listItemObserver() //запускаем основной обсервер
                return true
            }

        }
    }

    private fun init(){ //интент с помощью которого получаем из сущности данные
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when(state){
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem) //для редактирования подсказок
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name) //для нажатия на подсказку
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> { //для удаления подсказок
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") //после удаления список подсказок удаляется
            }
        }
    }

    private fun editListItem(item: ShopListItem){
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) { //возвращает перезаписанные данные в базу
                mainViewModel.updateListItem(item)
            }
        })
    }
    private fun editLibraryItem(item: ShopListItem){ //для редактирования подсказок
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) { //возвращает перезаписанные данные в базу
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") //после обновления что будет показываться
            }
        })
    }

    private fun saveItemCount(){ //считает количество продуктов в списке, 48 урок
        var checkedItemCounter = 0 //счетчик сколько элементов отмечено
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++//сколько элементов в адаптере, с отмеченным чекбоксом, столько и будет счетчик++
        }
        val tempShopListNameItem = shopListNameItem?.copy( //копируем данные из сущности во временную переменную
            allItemCounter = adapter?.itemCount!!,  //в адаптере есть количество элементов всего
            checkedItemsCounter = checkedItemCounter
        )
        mainViewModel.updateListName(tempShopListNameItem!!) //обновляем сущность с помощью майнВьюМодел
    }

    override fun onBackPressed() { //отслеживаем нажатие кнопки назад, урок 48
        saveItemCount() //когда нажал кнопку назад все записалось в базу данных
        super.onBackPressed()
    }

}