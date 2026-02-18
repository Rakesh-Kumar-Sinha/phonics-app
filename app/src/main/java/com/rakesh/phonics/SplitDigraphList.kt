package com.rakesh.phonics

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.DigraphsAdapter
import com.rakesh.phonics.adapters.SplitDigraphsAdapter
import com.rakesh.phonics.models.Digraph
import com.rakesh.phonics.models.SplitDigraphs

class SplitDigraphList : AppCompatActivity() {
    var list = ArrayList<SplitDigraphs>()
    private lateinit var adapter: SplitDigraphsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_digraph_list)
        window.statusBarColor = Color.parseColor("#026894")
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)
        val assetManager = this.assets
        val folders = assetManager.list("split_digraphs") ?: arrayOf()

        for (folderName in folders) {
            list.add(
                SplitDigraphs(
                    folderName.replace("_", " "),
                    "split_digraphs/$folderName/main/main.mp3",
                    "split_digraphs/$folderName/"
                )
            )
        }
         adapter = SplitDigraphsAdapter(this, list) { folderName ->
            val intent = Intent(this, SplitDigraphActivity::class.java)
            intent.putExtra("EXTRA_FOLDER_NAME", folderName)
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.di_graph_recy);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }
    override fun onStop() {
        super.onStop()
        adapter.releasePlayer()
    }

}