package com.nnnikitaaa.trap.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nnnikitaaa.trap.R
import com.nnnikitaaa.trap.datecard.DateCard
import com.nnnikitaaa.trap.datecard.DateCardAdapter
import com.nnnikitaaa.trap.db.AppDatabase
import com.nnnikitaaa.trap.db.HabitHistoryEntity
import com.nnnikitaaa.trap.db.toHabit
import com.nnnikitaaa.trap.habit.HabitAdapter
import com.nnnikitaaa.trap.habit.isHabitDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity() {

    private lateinit var dateActivityTitle: TextView
    private lateinit var addHabitButton: FloatingActionButton
    private lateinit var datesRecyclerView: RecyclerView
    private lateinit var habitRecyclerView: RecyclerView

    private var selectedDate = LocalDate.now()

    private lateinit var db: AppDatabase
    private val prefs by lazy { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    updateHabits(selectedDate)
                    result.data?.let {
                        val msg = it.getStringExtra("msg").orEmpty()
                        if (msg.isNotBlank()) {
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                msg,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        db = AppDatabase.getDatabase(this)

        addHabitButton = findViewById(R.id.addHabitButton)
        dateActivityTitle = findViewById(R.id.dateActivityTitle)
        datesRecyclerView = findViewById(R.id.datesRecyclerView)
        habitRecyclerView = findViewById(R.id.habitRecyclerView)

        addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            activityResultLauncher.launch(intent)
        }


        val dates = dateCardsAroundToday()
        datesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        datesRecyclerView.adapter = DateCardAdapter(dates, this, 3) { dateCard, _ ->
            selectedDate = dateCard.date
            updateDateTitle(selectedDate)
            updateHabits(selectedDate)
        }

        val previousLaunchDate = getPreviousLaunchDate()

        previousLaunchDate?.let {
            getDatesBetween(it, LocalDate.now()).forEach{ date ->
                storeDateHistory(date)
            }
        }

        saveLaunchDate(LocalDate.now())

        val historyDao = db.habitHistoryDao()
        habitRecyclerView.layoutManager = LinearLayoutManager(this)
        habitRecyclerView.adapter = HabitAdapter(mutableListOf(), this, onCheckBoxCheckedChange = { habit, _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                val historyEntity = historyDao.getHistory(selectedDate, habit.id)
                historyEntity?.let {
                    historyEntity.completed = isChecked
                    historyDao.updateHistory(historyEntity)
                } ?: run {
                    val newHistory = HabitHistoryEntity(
                        habitId = habit.id,
                        date = selectedDate,
                        completed = isChecked
                    )
                    historyDao.insertHistory(newHistory)
                }
            }
        }, onClicked = { habit, _ ->
            val intent = Intent(this, UpdateHabitActivity::class.java)
            intent.putExtra("id", habit.id)
            intent.putExtra("name", habit.name)
            intent.putExtra("periodName", habit.period.name)
            intent.putExtra("startDate", habit.startDate.toEpochDay())
            activityResultLauncher.launch(intent)
        } )
        updateHabits(selectedDate)
    }

    private fun updateHabits(date: LocalDate) {
        if (date > LocalDate.now()) {
            val habitDao = db.habitDao()
            CoroutineScope(Dispatchers.IO).launch {
                val habitEntities = habitDao.getAllHabits(date)
                val filteredHabits = habitEntities
                    .map { it.toHabit(completed = false, enabled = false, clickable = true) }
                    .filter { isHabitDay(it, date) }

                withContext(Dispatchers.Main) {
                    val adapter = habitRecyclerView.adapter as HabitAdapter
                    adapter.updateHabits(filteredHabits)
                }
            }
        } else if (date == LocalDate.now()) {
            val historyDao = db.habitHistoryDao()
            val habitDao = db.habitDao()

            CoroutineScope(Dispatchers.IO).launch {
                val historyEntities = historyDao.getHistoryForDate(date)
                val habitEntities = habitDao.getAllHabits(date)

                val excludeIds = historyEntities.map { it.habitId }.toSet()
                val filteredHabits = habitEntities
                    .filterNot { habit -> excludeIds.contains(habit.id) }
                    .map { it.toHabit(completed = false, enabled = true, clickable = true) }
                    .filter { isHabitDay(it, date) }

                val habitMap = habitEntities.associateBy { it.id }
                val historyHabits = historyEntities.mapNotNull { history ->
                    habitMap[history.habitId]?.let { history.toHabit(it, enabled = true, clickable = true) }
                }

                val combinedHabits = (filteredHabits + historyHabits).sortedBy { it.id }

                withContext(Dispatchers.Main) {
                    val adapter = habitRecyclerView.adapter as HabitAdapter
                    adapter.updateHabits(combinedHabits)
                }
            }
        } else {
            val historyDao = db.habitHistoryDao()
            val habitDao = db.habitDao()

            CoroutineScope(Dispatchers.IO).launch {
                val historyEntities = historyDao.getHistoryForDate(date)
                val habitEntities = habitDao.getAllHabits(date)

                val habitMap = habitEntities.associateBy { it.id }
                val historyHabits = historyEntities.mapNotNull { history ->
                    habitMap[history.habitId]?.let { history.toHabit(it, enabled = false, clickable = false) }
                }

                withContext(Dispatchers.Main) {
                    val adapter = habitRecyclerView.adapter as HabitAdapter
                    adapter.updateHabits(historyHabits)
                }
            }
        }
    }

    private fun storeDateHistory(date: LocalDate) {
        val historyDao = db.habitHistoryDao()
        val habitDao = db.habitDao()

        CoroutineScope(Dispatchers.IO).launch {
            val habitEntities = habitDao.getAllHabits(date)
            habitEntities.forEach {
                if (historyDao.getHistory(date, it.id) == null) {
                    historyDao.insertHistory(
                        HabitHistoryEntity(
                            habitId = it.id,
                            date = date,
                            completed = false
                        )
                    )
                }
            }
        }
    }

    private fun updateDateTitle(date: LocalDate) {
        dateActivityTitle.text = when (date) {
            LocalDate.now() -> getString(R.string.today)
            LocalDate.now().plusDays(1) -> getString(R.string.tomorrow)
            LocalDate.now().plusDays(-1) -> getString(R.string.yesterday)
            else -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        }
    }

    private fun dateCardsAroundToday(): List<DateCard> {
        val dateCards = mutableListOf<DateCard>()
        for (i in -3L..3L) {
            dateCards.add(DateCard(LocalDate.now().plusDays(i)))
        }
        return dateCards
    }

    private fun getPreviousLaunchDate(): LocalDate? {
        val previousDateString = prefs.getString("previous_launch_date", null)
        return previousDateString?.let { LocalDate.parse(it) }
    }

    private fun saveLaunchDate(date: LocalDate) {
        prefs.edit().putString("previous_launch_date", date.toString()).apply()
    }

    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate

        while (currentDate <= endDate) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return dates
    }
}
