package com.rakesh.phonics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper
import com.rakesh.phonics.models.LSM

class LearnSingleLetterActivity : AppCompatActivity() {
    private lateinit var myAdapter: LearnSingleLetterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_single_letter)
        window.statusBarColor = Color.parseColor("#026894")
//        val letters = List(26) { i ->
//            val letter = ('A' + i).toString()
//            val audioPath = "alphabets/${'A' + i}.mp3"
//            LetterData(letter, audioPath)
//        }.shuffled()
        val letters = ArrayList<LSM>();
        letters.add(LSM("A", "learn_single/A-ANT.mp3",8000,"🐜"))
        letters.add(LSM("M", "learn_single/M-moon.mp3",8000,"🌙"))
        letters.add(LSM("T", "learn_single/T-TUB.mp3",8000,"🛁"))
        letters.add(LSM("N", "learn_single/N-nose.mp3",9000,"👃"))
        letters.add(LSM("B", "learn_single/B-BALL.mp3",8000,"🏀"))
        letters.add(LSM("P", "learn_single/P-PEN.mp3",8000,"🖊️"))
        letters.add(LSM("S", "learn_single/S-sun.mp3",8000,"☀️"))
        letters.add(LSM("U", "learn_single/U-up.mp3",8000,"⬆️"))
        letters.add(LSM("O", "learn_single/O-orange.mp3",9000,"🍊"))
        letters.add(LSM("C", "learn_single/C-CAT.mp3",8000,"🐱"))
        letters.add(LSM("E", "learn_single/E-EGG.mp3",8000,"🥚"))
        letters.add(LSM("D", "learn_single/D-DOG.mp3",8000,"🐶"))
        letters.add(LSM("H", "learn_single/H-hut.mp3",9000,"🏠"))
        letters.add(LSM("J", "learn_single/J-jug.mp3",8000,"🍶"))
        letters.add(LSM("I", "learn_single/I-INK.mp3",8000,"🖋️"))
        letters.add(LSM("G", "learn_single/G-GIRL.mp3",8000,"👧"))
        letters.add(LSM("F", "learn_single/F-FORK.mp3",8000,"🍴"))
        letters.add(LSM("K", "learn_single/K-KING.mp3",8000,"🤴"))
        letters.add(LSM("R", "learn_single/R-RAT.mp3",8000,"🐭"))
        letters.add(LSM("L", "learn_single/L-LION.mp3",8000,"🦁"))
        letters.add(LSM("Q", "learn_single/Q-QUEEN.mp3",8000,"👸"))
        letters.add(LSM("X", "learn_single/X-BOX.mp3",8000,"📦"))
        letters.add(LSM("V", "learn_single/V-voilen.mp3",9000,"🎻"))
        letters.add(LSM("W", "learn_single/W-WOOL.mp3",8000,"🧶"))
        letters.add(LSM("Y", "learn_single/Y-YAK.mp3",8000,"🐂"))
        letters.add(LSM("Z", "learn_single/Z-zip.mp3",9000,"🧷"))






















        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val slideNumber: TextView = findViewById(R.id.slideNumber)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = LearnSingleLetterAdapter(this, letters)
        viewPager.adapter = myAdapter
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

                        // Detect drag starting while already on last page
                        if (currentPosition == lastPosition) {
                            dragStartedOnLast = true
                        }
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {

                        // If drag started on last AND still on last
                        // → user attempted to scroll beyond content
                        if (isUserDragging &&
                            dragStartedOnLast &&
                            currentPosition == lastPosition
                        ) {
                            FinalPopupHelper.show(this@LearnSingleLetterActivity)
                        }

                        isUserDragging = false
                        dragStartedOnLast = false
                    }
                }
            }
        })
    }
    override fun onPause() {
        super.onPause()
        // stop audio when app goes to background
        myAdapter.releasePlayer()
    }

}