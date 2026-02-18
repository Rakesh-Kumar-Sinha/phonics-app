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

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Update slide number
                var finalShow = "${position + 1} / ${letterList.size}"

                if (position == 0) {
                    finalShow += "  Swipe ➡"
                }

                swipeHint.text = finalShow


                // Show swipe hint only on first page
//                swipeHint.visibility = if (position == 0) View.VISIBLE else View.GONE
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