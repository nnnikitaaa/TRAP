package com.nnnikitaaa.trap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val msg = it.getStringExtra("msg").orEmpty()
                    if (msg.isNotBlank())
                    {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val addHabitButton = findViewById<FloatingActionButton>(R.id.addHabitButton)
        addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }
}