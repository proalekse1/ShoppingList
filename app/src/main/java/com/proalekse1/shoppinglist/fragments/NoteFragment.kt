package com.proalekse1.shoppinglist.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.proalekse1.shoppinglist.activities.MainApp
import com.proalekse1.shoppinglist.activities.NewNoteActivity
import com.proalekse1.shoppinglist.databinding.FragmentNoteBinding
import com.proalekse1.shoppinglist.db.MainViewModel
import com.proalekse1.shoppinglist.db.NoteAdapter
import com.proalekse1.shoppinglist.entities.NoteItem


class NoteFragment : BaseFragment(), NoteAdapter.Listener { //
    private lateinit var binding: FragmentNoteBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent> //переменная для лаунчера
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences //для настроек

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java)) //запускаем новое активити для новой заметки и ждем назад результат записи
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult() // запускаем лаунчер
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //запускаем ресайклер вью
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer() // запускаем обсервер который будет слушать изменения в базе данных
    }

    private fun initRcView() = with(binding){ //инициализируем адаптер и ресайклервью
        defPref = PreferenceManager.getDefaultSharedPreferences(activity)
        rcViewNote.layoutManager = getLayoutManager() //выбор стиля разметки
        adapter = NoteAdapter(this@NoteFragment, defPref) // интерфейс кнопки удаления элемента
        rcViewNote.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager{ //для настроек стиля рзаметки список или плитка
        return if(defPref.getString("note_style_key", "Linear") == "Linear"){
            LinearLayoutManager(activity)
        } else {
            //грид разметка в одной строке 2 заметки и все заметки по ертикали
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun observer(){   //слушатель изменений в базе данных
        mainViewModel.allNotes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun onEditResult(){                 //инициализируем лаунчер который будет ждать результат нажатия
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if(editState == "update"){
                    mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                }

            }
        }
    }


    override fun deleteItem(id: Int) { //функция удаления заметки
        mainViewModel.deleteNote(id)
    }

    override fun onClickItem(note: NoteItem) { //функция изменения заметки
        val intent = Intent(activity, NewNoteActivity::class.java).apply { //создаем интент
            putExtra(NEW_NOTE_KEY, note)
        }
        editLauncher.launch(intent) //с помощью лаунчера ждем назад изменений
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key" //ключ для новой заметки
        const val EDIT_STATE_KEY = "edit_state_key" //ключ для лаунчера для обновления заметки
        @JvmStatic
        fun newInstance() = NoteFragment()
    }

}