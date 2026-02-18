package com.rakesh.phonics

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.GraphAdapter

class GraphsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_list)
        window.statusBarColor = Color.parseColor("#026894")
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)
        val type = intent.getStringExtra("TYPE") ?: "trigraphs"
        val recycler = findViewById<RecyclerView>(R.id.recyclerGraphs)
        recycler.layoutManager = LinearLayoutManager(this)
        val folders = assets.list(type)?.toList() ?: emptyList()

        recycler.adapter = GraphAdapter(this, type, folders)

    }
    override fun onStop() {
        super.onStop()
        SoundPlayer.stop()
    }

}