package com.nnnikitaaa.trap.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.nnnikitaaa.trap.R
import com.nnnikitaaa.trap.db.AppDatabase
import com.nnnikitaaa.trap.db.HabitEntity
import com.nnnikitaaa.trap.db.HabitHistoryEntity
import com.nnnikitaaa.trap.db.toHabit
import com.nnnikitaaa.trap.habit.PeriodType
import com.nnnikitaaa.trap.habit.isHabitDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UpdateHabitActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var periodGroup: RadioGroup

    private var nameInputted = true
    private var dateInputted = true
    private var periodInputted = true

    private lateinit var date: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_habit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val habitId = intent.getLongExtra("id", -1L)
        val habitName = intent.getStringExtra("name")
        val habitPeriod = PeriodType.valueOf(intent.getStringExtra("periodName").orEmpty())
        val habitStartDate = LocalDate.ofEpochDay(intent.getLongExtra("startDate", -1L))

        val longDateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

        val today = LocalDate.now()
        val db = AppDatabase.getDatabase(this)
        val habitDao = db.habitDao()
        val historyDao = db.habitHistoryDao()

        progressBar = findViewById(R.id.progressBar)
        nameInputLayout = findViewById(R.id.nameInputLayout)
        dateInputLayout = findViewById(R.id.dateInputLayout)
        periodGroup = findViewById(R.id.periodGroup)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        updateProgressBar()

        nameInputLayout.editText?.setText(habitName)
        periodGroup.check(periodTypeToRadio(habitPeriod))

        val dateEditText = dateInputLayout.editText

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    LocalDate.of(selectedYear, Month.of(selectedMonth + 1), selectedDay)
                date = selectedDate
                dateEditText?.setText(selectedDate.format(longDateFmt))
            },
            habitStartDate.year,
            habitStartDate.monthValue - 1,
            habitStartDate.dayOfMonth
        )

        date = habitStartDate
        dateEditText?.let {
            it.setText(date.format(longDateFmt))
            it.inputType = InputType.TYPE_NULL
            it.keyListener = null
            it.isFocusable = false
            it.isClickable = true
            it.setOnClickListener {
                datePickerDialog.show()
            }
        }

        val nameEditText = nameInputLayout.editText
        nameEditText?.addTextChangedListener { text ->
            val newNameInputted = !text.isNullOrBlank()

            if (nameInputted != newNameInputted) {
                nameInputted = newNameInputted
                updateProgressBar()
            }
        }

        periodGroup.setOnCheckedChangeListener { _, _ ->
            if (periodInputted) {
                return@setOnCheckedChangeListener
            }
            periodInputted = true
            updateProgressBar()
        }

        saveButton.setOnClickListener {
            if (listOf(dateInputted, nameInputted, periodInputted).count { it } < 3) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.habit_add_not_all_fields),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val name = nameEditText?.text.toString().trim()
            val period = radioToPeriodType(periodGroup.checkedRadioButtonId)

            CoroutineScope(Dispatchers.IO).launch {
                val habitEntity = habitDao.getHabit(name, excludeId = habitId)
                if (habitEntity != null) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.habit_name_already_exists),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val newHabitEntity = HabitEntity(
                    id = habitId,
                    name = name,
                    period = period,
                    startDate = date,
                    active = true
                )

                habitDao.updateHabit(newHabitEntity)

                withContext(Dispatchers.Main) {
                    val returnIntent = Intent().apply {
                        putExtra("msg", getString(R.string.habit_updated_msg))
                    }
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            }
        }

        deleteButton.setOnClickListener {
            val name = nameEditText?.text.toString().trim()
            val period = radioToPeriodType(periodGroup.checkedRadioButtonId)

            CoroutineScope(Dispatchers.IO).launch {
                val habitToDelete = HabitEntity(
                    id = habitId,
                    name = name,
                    period = period,
                    startDate = date,
                    active = true
                )
                habitDao.deleteHabit(habitToDelete)

                withContext(Dispatchers.Main) {
                    val returnIntent = Intent().apply {
                        putExtra("msg", getString(R.string.habit_deleted_msg))
                    }
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            }
        }
    }

    private fun radioToPeriodType(radioId: Int): PeriodType {
        return when (radioId) {
            R.id.period_daily -> PeriodType.DAILY
            R.id.period_weekly -> PeriodType.WEEKLY
            R.id.period_monthly -> PeriodType.MONTHLY
            R.id.period_weekdays -> PeriodType.WEEKDAYS
            R.id.period_weekends -> PeriodType.WEEKENDS
            else -> PeriodType.DAILY
        }
    }

    private fun periodTypeToRadio(period: PeriodType): Int {
        return when (period) {
            PeriodType.DAILY -> R.id.period_daily
            PeriodType.WEEKLY -> R.id.period_weekly
            PeriodType.MONTHLY -> R.id.period_monthly
            PeriodType.WEEKDAYS -> R.id.period_weekdays
            PeriodType.WEEKENDS -> R.id.period_weekends
        }
    }
    private fun updateProgressBar() {
        val progress = listOf(dateInputted, nameInputted, periodInputted).count { it }
        progressBar.progress = progress
    }
}