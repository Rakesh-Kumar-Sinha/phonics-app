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
import com.rakesh.phonics.adapters.DoubleLetterAdapter
import com.rakesh.phonics.models.DoubleLetter
import com.rakesh.phonics.models.LSM

class LearnDoubleLetter : AppCompatActivity() {
    private lateinit var myAdapter: DoubleLetterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_double_letter)

        window.statusBarColor = Color.parseColor("#026894")
        val letters = ArrayList<DoubleLetter>();
        letters.add(DoubleLetter("A","M","learn_double/AM.mp3"))
        letters.add(DoubleLetter("a","n","learn_double/an.mp3"))
        letters.add(DoubleLetter("A","S","learn_double/AS.mp3"))
        letters.add(DoubleLetter("A","T","learn_double/AT.mp3"))
        letters.add(DoubleLetter("I","N","learn_double/IN.mp3"))
        letters.add(DoubleLetter("I","S","learn_double/IS.mp3"))
        letters.add(DoubleLetter("O","F","learn_double/OF.mp3"))
        letters.add(DoubleLetter("O","N","learn_double/ON.mp3"))
        letters.add(DoubleLetter("O","R","learn_double/OR.mp3"))
        letters.add(DoubleLetter("O","X","learn_double/OX.mp3"))
        letters.add(DoubleLetter("U","P","learn_double/UP.mp3"))
        letters.add(DoubleLetter("U","S","learn_double/US.mp3"))
        letters.add(DoubleLetter("A","D","learn_double/AD-ALIEN.mp3"))
        letters.add(DoubleLetter("E","D","learn_double/ED-ALIEN.mp3"))
        letters.add(DoubleLetter("E","G","learn_double/EG-ALIEN.mp3"))
        letters.add(DoubleLetter("E","T","learn_double/ET-ALIEN.mp3"))
        letters.add(DoubleLetter("I","G","learn_double/IG-ALIEN.mp3"))
        letters.add(DoubleLetter("I","P","learn_double/IP-ALIEN.mp3"))
        letters.add(DoubleLetter("I","X","learn_double/IX-ALIEN.mp3"))
        letters.add(DoubleLetter("O","G","learn_double/OG-ALIEN.mp3"))
        letters.add(DoubleLetter("O","P","learn_double/OP-ALIEN.mp3"))
        letters.add(DoubleLetter("O","T","learn_double/OT-ALIEN.mp3"))
        letters.add(DoubleLetter("U","G","learn_double/UG-ALIEN.mp3"))
        letters.add(DoubleLetter("U","M","learn_double/UM-ALIEN.mp3"))
        letters.add(DoubleLetter("U","N","learn_double/UN-ALIEN.mp3"))
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
//        val slideNumber: TextView = findViewById(R.id.slideNumber)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = DoubleLetterAdapter(this, letters)
        viewPager.adapter = myAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Initial UI
//        slideNumber.text = "1 / ${letters.size}"
        swipeHint.visibility = View.VISIBLE

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Update slide number
//                slideNumber.text = "${position + 1} / ${letters.size}"

                // Show swipe hint only on first page
                swipeHint.visibility = if (position == 0) View.VISIBLE else View.GONE
            }
        })

    }
    override fun onPause() {
        super.onPause()
        // stop audio when app goes to background
        myAdapter.releasePlayer()
    }
}