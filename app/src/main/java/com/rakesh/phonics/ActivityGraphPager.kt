package com.rakesh.phonics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rakesh.phonics.adapters.GraphPagerAdapter
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper

class ActivityGraphPager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_pager)
        window.statusBarColor = Color.parseColor("#026894")
        val type = intent.getStringExtra("TYPE")!!
        val folder = intent.getStringExtra("FOLDER")!!

        val files = assets.list("$type/$folder")
            ?.filter { it.endsWith(".mp3") } ?: emptyList()

        val pager = findViewById<ViewPager2>(R.id.viewPager)
        pager.adapter = GraphPagerAdapter(this, type, folder, files)
        Log.d("Hellp","asdas");
        TabLayoutMediator(
            findViewById(R.id.indicator),
            pager
        ) { _, _ -> }.attach()

        var currentPosition = 0
        val lastPosition = (pager.adapter?.itemCount ?: 0) - 1
        var isUserDragging = false

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {

                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        isUserDragging = true
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {

                        if (isUserDragging && currentPosition == lastPosition) {

                            // ✅ One line reusable call
                            FinalPopupHelper.show(this@ActivityGraphPager)

                        }

                        isUserDragging = false
                    }
                }
            }
        })

    }
    override fun onStop() {
        super.onStop()
        SoundPlayer.stop()
    }

}