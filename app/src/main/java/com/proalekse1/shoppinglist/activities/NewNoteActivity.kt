package com.proalekse1.shoppinglist.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.proalekse1.shoppinglist.R
import com.proalekse1.shoppinglist.databinding.ActivityNewNoteBinding
import com.proalekse1.shoppinglist.entities.NoteItem
import com.proalekse1.shoppinglist.fragments.NoteFragment
import com.proalekse1.shoppinglist.utils.HtmlManager
import com.proalekse1.shoppinglist.utils.MyTouchListener
import com.proalekse1.shoppinglist.utils.TimeManager
import java.util.*


class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var defPref: SharedPreferences // для настроек
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null //для настроек

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)//доступ для настроек
        setTheme(getSelectedTheme()) //изменение темы
        setContentView(binding.root)
        actionBarSettings() //запускаем функцию показа стрелки назад в экшн баре
        getNote()
        init()
        setTextSize() //для измнения размера текста
        onClickColorPicker()
    }

    private fun onClickColorPicker() = with(binding) { //слушатель нажатий колор пикера
        imRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        imBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black)
        }
        imBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        imYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_yellow)
        }
        imGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        imOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        binding.colorPicker.setOnTouchListener(MyTouchListener()) //инииализируем движтель колор пикера
        pref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем настройки
    }

    private fun getNote(){ //получаем уже существующую заметку из базы
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if(sNote != null){
        note = sNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding){
            edTitle.setText(note?.title)
            edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim()) //текст из базы данных превращаем в HTML
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //подключаем меню в активити
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //слушатель нажатий для меню
        if(item.itemId == R.id.id_save){ //жмем кнопку save
            setMainResult()
        }else if(item.itemId == android.R.id.home){ // если нажимаем стрелочку, активити закрывается
            finish()
        }else if(item.itemId == R.id.id_bold){ // если нажимаем B шрифт меняется
            setBoldForSelectedText()
        }else if(item.itemId == R.id.id_color){ // если нажимаем появляется меню изменения цвета
            if(binding.colorPicker.isShown){ // если колорпикер показан то закрываем
                closeColorPicker()
            }else { //иначе закрывать колор пикер
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) { // изменение стиля текста
        val startPos = edDescription.selectionStart //взяли позицию начала выделения текста
        val endPos = edDescription.selectionEnd //взяли позицию конца выделения текста

        val styles = edDescription.text.getSpans(startPos, endPos, StyleSpan::class.java) //переменная хранит стиль текста
        var boldStyle: StyleSpan? = null
        if(styles.isNotEmpty()){ // если стиль уже жирный, то убрать стиль
            edDescription.text.removeSpan(styles[0])
        } else { //если не жирный указываем ему жирный
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        edDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim() //функция которая удаляет все пробелы из HTML
        edDescription.setSelection(startPos) //курсор перемещается на старт позишион
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) { // изменение цвета, в скобки будем передавать какой цвет выбрали
        val startPos = edDescription.selectionStart //взяли позицию начала выделения текста
        val endPos = edDescription.selectionEnd //взяли позицию конца выделения текста

        val styles = edDescription.text.getSpans(
            startPos,
            endPos,
            ForegroundColorSpan::class.java
        ) //переменная хранит цвет текста
        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0]) //если стиль не пустой удаляем его

        edDescription.text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)),
            startPos,
            endPos,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edDescription.text.trim() //функция которая удаляет все пробелы из HTML
        edDescription.setSelection(startPos) //курсор перемещается на старт позишион
    }

    private fun setMainResult(){ //функция для лаунчера чтобы по ключам вернуть занчение в NoteFragment
        var editState = "new" //ключ для передачи в putExtra
        val tempNote: NoteItem?
        if(note == null){ // проверка обновлять или создавать новую заметку
            tempNote = createNewNote()
        } else {
            editState = "update"
            tempNote = updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)

        }
        setResult(RESULT_OK, i)
        finish() //после того как все положили в лаунчер закрываем активити
    }

    private fun updateNote() : NoteItem? = with(binding){ //функция обновления заметки
         return  note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text) //превращаем в стринг и передаем в базу данных
            )
    }

    private fun createNewNote(): NoteItem { // функция заполнения NoteItem
        return NoteItem(
            null, // id генерируется автоматом поэтому не указываем ничего
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text), //изменили HTML в текст и передали в базу
            TimeManager.getCurrentTime(),
            ""

        )
    }

    private fun actionBarSettings(){ //активируем стрелку назад  нашем активити
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker(){
        binding.colorPicker.visibility = View.VISIBLE //делаем пикер видимым
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker(){ //закрываем колор пикер
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : Animation.AnimationListener{ //слушатель нажатий
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) { //анимация закончилась
                binding.colorPicker.visibility = View.GONE //делаем пикер невидимым
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallback(){ //убираем меню при выделении текста
        val actionCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear() //меню рисуется и стирается
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear() //меню рисуется и стирается
                return true
            }
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return true
            }
            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }
        binding.edDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun setTextSize() = with(binding){ //функция изменения размера текста
        edTitle.setTextSize(pref?.getString("title_size_key", "16")) //в скобках ключ настройки и размер текста по умолчанию
        edDescription.setTextSize(pref?.getString("content_size_key", "14"))//настройки для содержания
    }

    private fun EditText.setTextSize(size: String?){ //extention для изменения размеа текста в заметках
        if(size != null) this.textSize = size.toFloat()
    }

    private fun getSelectedTheme(): Int{ //считываем настройки темы
        return if(defPref.getString("theme_key", "blue") == "blue"){ //ключ и значение по умолчанию
            R.style.Theme_NewNoteBlue
        } else {
            R.style.Theme_NewNoteRed
        }
    }

}


















