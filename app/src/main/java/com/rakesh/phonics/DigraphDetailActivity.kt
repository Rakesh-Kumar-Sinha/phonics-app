package com.rakesh.phonics

import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.DigraphsDetailAdapter
import com.rakesh.phonics.helpers.FinalPopupHelper.FinalPopupHelper
import com.rakesh.phonics.models.Digraph

class DigraphDetailActivity : AppCompatActivity() {
    private lateinit var myAdapter: DigraphsDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digraph_detail)
        window.statusBarColor = Color.parseColor("#026894")
        val folderName = intent.getStringExtra("EXTRA_FOLDER_NAME")
        Log.d("TEST",folderName.toString())
        if (folderName == null) {
            finish()
            return
        }
//        val basePath = "$folderName"
        val mp3List = this.assets.list(folderName)?:arrayOf()
        Log.d("TEST",mp3List.toString())
        val items = ArrayList<Digraph>()
        for (p in mp3List) {
            // create a title from file name
            if(p=="main"){
                continue
            }
            val fileTitle = p.substringAfterLast("/").substringBeforeLast(".").replace("_", " ")
            items.add(Digraph(fileTitle, "/$folderName"+p, ""))
        }
        Log.d("TML","hellop")
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val slideNumber: TextView = findViewById(R.id.slideNumber)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = DigraphsDetailAdapter(this, items)
        viewPager.adapter = myAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Initial UI
        slideNumber.text = "1 / ${items.size}"
        swipeHint.visibility = View.VISIBLE

        var currentPosition = 0
        val lastPosition = items.size - 1
        var isUserDragging = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPosition = position

                // Update slide number
                slideNumber.text = "${position + 1} / ${items.size}"

                // Show swipe hint only on first page
                swipeHint.visibility = if (position == 0) View.VISIBLE else View.GONE
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

                            FinalPopupHelper.show(this@DigraphDetailActivity)

                        }

                        isUserDragging = false
                    }
                }
            }

        })

    }

}