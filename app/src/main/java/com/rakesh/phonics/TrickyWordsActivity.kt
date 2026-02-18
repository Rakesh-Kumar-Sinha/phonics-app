package com.rakesh.phonics
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rakesh.phonics.adapters.DoubleLetterAdapter
import com.rakesh.phonics.adapters.TrickyWordsAdapter
import com.rakesh.phonics.models.SplitDigraphs
import com.rakesh.phonics.models.TrickyWord

class TrickyWordsActivity : AppCompatActivity() {
    private lateinit var myAdapter: TrickyWordsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tricky_words)
        window.statusBarColor = Color.parseColor("#026894")
        val assetManager = this.assets
        val folders = assetManager.list("tricky_words") ?: arrayOf()
        Log.d("TEST",folders.toString())
        val list = ArrayList<TrickyWord>()
        for (fileName in folders) {
            list.add(
                TrickyWord(
                    fileName.substringBeforeLast("."),
                    "tricky_words/$fileName",
                )
            )
        }
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val swipeHint: TextView = findViewById(R.id.swipeHint)
        myAdapter = TrickyWordsAdapter(this, list)
        viewPager.adapter = myAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        swipeHint.visibility = View.VISIBLE

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                swipeHint.visibility = if (position == 0) View.VISIBLE else View.GONE
            }
        })



    }

//    override fun onPause() {
//        super.onPause()
//        adapter.stopPlayer()
//    }
//
//    override fun onBackPressed() {
//        adapter.stopPlayer()
//        super.onBackPressed()
//    }
}
