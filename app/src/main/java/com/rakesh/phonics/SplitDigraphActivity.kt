package com.rakesh.phonics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.SplitDigraphPagerAdapter
import com.rakesh.phonics.models.Digraph
import com.rakesh.phonics.models.SplitDigraphItem
import com.rakesh.phonics.models.SplitDigraphs

class SplitDigraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_digraph)
        window.statusBarColor = Color.parseColor("#026894")
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val folderName = intent.getStringExtra("EXTRA_FOLDER_NAME")

        if (folderName == null) {
            finish()
            return
        }
        val parts = folderName.split("_")
        val lastPart = parts.lastOrNull() ?: ""

        if (lastPart.length < 2) {
            throw IllegalArgumentException("Folder name last part must have at least 2 characters")
        }

        val split1 = lastPart[0]
        val split2 = lastPart[1]

        val mp3List = assets.list(folderName) ?: emptyArray()

        val items = mutableListOf<SplitDigraphItem>()

        for (p in mp3List) {
            // Skip "main" file
            if (p == "main") continue

            val splitName = p.substringBeforeLast(".")

            val firstIndex = splitName.indexOf(split1)
//            val secIndex = splitName.indexOf(split2)
            val secIndex = if (split1 == split2 && firstIndex != -1) {
                splitName.indexOf(split2, firstIndex + 1)
            } else {
                splitName.indexOf(split2)
            }

            if (splitName.lowercase() == "extreme") {
                items.add(
                    SplitDigraphItem(
                        splitName,
                        listOf(4, 6),
                        "$folderName" + "main/sub.mp3",
                        "$folderName$p",
                    )
                )

            } else if (splitName.lowercase() == "inside") {
                items.add(
                    SplitDigraphItem(
                        splitName,
                        listOf(3, 5),
                        "$folderName" + "main/sub.mp3",
                        "$folderName$p",
                    )
                )
            } else {
                items.add(
                    SplitDigraphItem(
                        splitName,
                        listOf(firstIndex, secIndex),
                        "$folderName" + "main/sub.mp3",
                        "$folderName$p",
                    )
                )
            }

        }
//
        viewPager.adapter = SplitDigraphPagerAdapter(this, items)
    }

    override fun onStop() {
        super.onStop()
        com.rakesh.phonics.SoundPlayer.stop()
    }
}