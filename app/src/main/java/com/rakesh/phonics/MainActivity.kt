package com.rakesh.phonics

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = Color.parseColor("#026894")

        val letters = List(26) { i ->
            val letter = ('A' + i).toString()
            val audioPath = "alphabets/${'A' + i}.mp3"
            LetterData(letter, audioPath)
        }.shuffled()

        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val slideNumber: TextView = findViewById(R.id.slideNumber)
        val swipeHint: TextView = findViewById(R.id.swipeHint)

        viewPager.adapter = LetterAdapter(this, letters)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Initial UI
        slideNumber.text = "1 / ${letters.size}"
        swipeHint.visibility = View.VISIBLE

        var currentPosition = 0
        var isUserDragging = false
        var dragStartedOnLast = false
        val lastPosition = letters.size - 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPosition = position

                // Update slide number
                slideNumber.text = "${position + 1} / ${letters.size}"

                // Show swipe hint only on first page
                swipeHint.visibility = if (position == 0) View.VISIBLE else View.GONE
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {

                    ViewPager2.SCROLL_STATE_DRAGGING -> {

                        isUserDragging = true

                        // Detect drag started while already on last page
                        if (currentPosition == lastPosition) {
                            dragStartedOnLast = true
                        }
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {

                        // Only trigger if user tried to scroll beyond last page
                        if (isUserDragging &&
                            dragStartedOnLast &&
                            currentPosition == lastPosition
                        ) {
                            FinalPopupHelper.show(this@MainActivity)
                        }

                        isUserDragging = false
                        dragStartedOnLast = false
                    }
                }
            }
        })
    }

}