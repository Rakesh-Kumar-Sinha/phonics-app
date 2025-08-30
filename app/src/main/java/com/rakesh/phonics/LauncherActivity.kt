package com.rakesh.phonics

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)


        val button = findViewById<MaterialButton>(R.id.btn_learn_single_letter_sound)
        button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}