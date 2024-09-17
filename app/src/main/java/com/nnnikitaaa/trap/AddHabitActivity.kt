package com.nnnikitaaa.trap

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class AddHabitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_habit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val longDateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

        val localDate = LocalDate.now()

        val saveButton = findViewById<Button>(R.id.saveButton)
        val dateEditText = findViewById<TextInputLayout>(R.id.dateInputLayout).editText

        saveButton.setOnClickListener {
            val returnIntent = Intent()
            val message = getString(R.string.habit_added_msg)
            returnIntent.putExtra("msg", message)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        dateEditText?.let{
            dateEditText.setText(localDate.format(longDateFmt))
            dateEditText.inputType = InputType.TYPE_NULL
            dateEditText.keyListener = null
            dateEditText.isFocusable = false
            dateEditText.isClickable = true
            dateEditText.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val selectedDate = LocalDate.of(selectedYear, Month.of(selectedMonth), selectedDay)
                        dateEditText.setText(selectedDate.format(longDateFmt))
                    },
                    localDate.year,
                    localDate.monthValue,
                    localDate.dayOfMonth
                )
                datePickerDialog.show()
            }
        }
    }
}