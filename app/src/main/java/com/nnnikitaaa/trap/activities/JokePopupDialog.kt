package com.nnnikitaaa.trap.activities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.nnnikitaaa.trap.R

class JokePopupDialog(context: Context, val top: String, val bottom: String) : Dialog(context) {
    private lateinit var jokeTop: TextView
    private lateinit var jokeBottom: TextView
    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.joke_popup)

        jokeTop = findViewById(R.id.jokeTop)
        jokeBottom = findViewById(R.id.jokeBottom)
        closeButton = findViewById(R.id.closeButton)

        jokeTop.text = top
        jokeBottom.text = bottom

        closeButton.setOnClickListener { dismiss() }
    }
}