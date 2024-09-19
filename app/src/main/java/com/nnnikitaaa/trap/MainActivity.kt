package com.nnnikitaaa.trap

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dateActivityTitle: TextView
    private lateinit var addHabitButton: FloatingActionButton
    private lateinit var datesRecyclerView: RecyclerView
    private lateinit var habitRecyclerView: RecyclerView

    private var selectedDate = LocalDate.now()

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
                    val data = result.data
                    data?.let {
                        val msg = it.getStringExtra("msg").orEmpty()
                        if (msg.isNotBlank()) {
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

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
        }

        val habits = mutableListOf<Habit>()
        habits.add(Habit("Idk", PeriodType.WEEKLY, LocalDate.now(), completed = true, enabled = false))
        habits.add(Habit("Idk2", PeriodType.WEEKLY, LocalDate.now(), false))
        habits.add(Habit("Idk", PeriodType.WEEKLY, LocalDate.now(), completed = true, enabled = false))
        habits.add(Habit("Idk2", PeriodType.WEEKLY, LocalDate.now(), false))
        habits.add(Habit("Idk", PeriodType.WEEKLY, LocalDate.now(), completed = true, enabled = false))
        habits.add(Habit("Idk2", PeriodType.WEEKLY, LocalDate.now(), false))
        habits.add(Habit("Idk", PeriodType.WEEKLY, LocalDate.now(), completed = true, enabled = false))
        habits.add(Habit("Idk2", PeriodType.WEEKLY, LocalDate.now(), false))

        habitRecyclerView.layoutManager = LinearLayoutManager(this)
        habitRecyclerView.adapter = HabitAdapter(habits, this)

        val today = LocalDate.now()
        val k =PeriodType.WEEKENDS.isHabitDay(LocalDate.now().minusDays(1), LocalDate.now().plusDays(5))
        Toast.makeText(this, k.toString(), Toast.LENGTH_LONG).show()
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
}
