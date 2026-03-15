package com.rakesh.phonics

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.FourLetterAdapter
import com.rakesh.phonics.adapters.ThreeLetterAdapter
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper
import com.rakesh.phonics.models.FourLetter
import com.rakesh.phonics.models.TripleLetter

class FourLetterActivity : AppCompatActivity() {
    private lateinit var myAdapter: FourLetterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_four_letter)
        val letterList = loadTripleLetters(this)
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = FourLetterAdapter(this, letterList)
        viewPager.adapter = myAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        swipeHint.visibility = View.VISIBLE

        var currentPosition = 0
        var isUserDragging = false
        var dragStartedOnLast = false
        val lastPosition = letterList.size - 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPosition = position

                // Update slide number
                var finalShow = "${position + 1} / ${letterList.size}"

                if (position == 0) {
                    finalShow += "  Swipe ➡"
                }

                swipeHint.text = finalShow
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {

                    ViewPager2.SCROLL_STATE_DRAGGING -> {

                        isUserDragging = true

                        // Detect if drag started while already on last page
                        if (currentPosition == lastPosition) {
                            dragStartedOnLast = true
                        }
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {

                        // If drag started on last AND still on last
                        // → user tried to scroll beyond content
                        if (isUserDragging &&
                            dragStartedOnLast &&
                            currentPosition == lastPosition
                        ) {
                            FinalPopupHelper.show(this@FourLetterActivity)
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
    fun loadTripleLetters(context: Context): ArrayList<FourLetter> {
        val letters = ArrayList<FourLetter>()
        try {
            val assetManager = context.assets
            val files = assetManager.list("learn_quadruple") ?: arrayOf()

            for (fileName in files) {
                if (fileName.endsWith(".mp3", ignoreCase = true)) {
                    val nameWithoutExt = fileName.substringBefore(".mp3")

                    // Ensure at least 3 letters before indexing
                    if (nameWithoutExt.length >= 3) {
                        val chars = nameWithoutExt.uppercase()
                        letters.add(
                            FourLetter(
                                chars[0].toString(),
                                chars[1].toString(),
                                chars[2].toString(),
                                chars[3].toString(),
                                "learn_quadruple/$fileName"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return letters
    }
}