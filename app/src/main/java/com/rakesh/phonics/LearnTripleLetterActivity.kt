package com.rakesh.phonics

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.DoubleLetterAdapter
import com.rakesh.phonics.adapters.ThreeLetterAdapter
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper
import com.rakesh.phonics.models.TripleLetter

class LearnTripleLetterActivity : AppCompatActivity() {
    private lateinit var myAdapter: ThreeLetterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_triple_letter)

//        val letters = ArrayList<TripleLetter>();
//        letters.add(TripleLetter("B","A","G","learn_triple/bag.mp3"))
//        letters.add(TripleLetter("B","A","T","learn_triple/bat.mp3"))
//        letters.add(TripleLetter("B","O","X","learn_triple/box.mp3"))
//        letters.add(TripleLetter("B","U","G","learn_triple/bug.mp3"))

        window.statusBarColor = Color.parseColor("#026894")
        val letterList = loadTripleLetters(this)
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = ThreeLetterAdapter(this, letterList)
        viewPager.adapter = myAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        swipeHint.visibility = View.VISIBLE

        var currentPosition = 0
        var lastPosition = letterList.size - 1
        var isUserDragging = false

//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//
//                var finalShow = "${position + 1} / ${letterList.size}"
//
//                if (position == 0) {
//                    finalShow += "  Swipe ➡"
//                }
//
//                swipeHint.text = finalShow
//
//            }
//        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPosition = position

                var finalShow = "${position + 1} / ${letterList.size}"

                if (position == 0) {
                    finalShow += "  Swipe ➡"
                } else if (position == lastPosition) {
                    finalShow += "  Swipe ➡ to finish 🎉"
                }

                swipeHint.text = finalShow
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {

                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        isUserDragging = true
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {

                        // User tried to swipe beyond last page
                        if (isUserDragging && currentPosition == lastPosition) {

                            FinalPopupHelper.show(this@LearnTripleLetterActivity)

                        }

                        isUserDragging = false
                    }
                }
            }

        })


    }

    private fun showFinalDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_final)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)


        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setGravity(Gravity.CENTER)

        val btnGoBack = dialog.findViewById<Button>(R.id.btnGoBack)

        btnGoBack.setOnClickListener {

            dialog.dismiss()

            finish()

        }

        dialog.show()
    }


    private fun playRandomFinalSound() {

        val files = assets.list("final") ?: return

        if (files.isEmpty()) return

        val randomFile = files.random()

        val afd = assets.openFd("final/$randomFile")

        val player = MediaPlayer()

        player.setDataSource(
            afd.fileDescriptor,
            afd.startOffset,
            afd.length
        )

        afd.close()

        player.prepare()
        player.start()

        player.setOnCompletionListener {
            it.release()
        }
    }


    override fun onPause() {
        super.onPause()
        // stop audio when app goes to background
        myAdapter.releasePlayer()
    }

    fun loadTripleLetters(context: Context): ArrayList<TripleLetter> {
        val letters = ArrayList<TripleLetter>()
        try {
            val assetManager = context.assets
            val files = assetManager.list("learn_tripple") ?: arrayOf()

            for (fileName in files) {
                if (fileName.endsWith(".mp3", ignoreCase = true)) {
                    val nameWithoutExt = fileName.substringBefore(".mp3")

                    // Ensure at least 3 letters before indexing
                    if (nameWithoutExt.length >= 3) {
                        val chars = nameWithoutExt.uppercase()
                        letters.add(
                            TripleLetter(
                                chars[0].toString(),
                                chars[1].toString(),
                                chars[2].toString(),
                                "learn_tripple/$fileName"
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