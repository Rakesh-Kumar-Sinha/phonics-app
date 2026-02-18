package com.rakesh.phonics

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.rakesh.phonics.adapters.DigraphsAdapter
import com.rakesh.phonics.models.Digraph

class DigraphsActivity : AppCompatActivity() {
    var list = ArrayList<Digraph>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagraphs)
        window.statusBarColor = Color.parseColor("#026894")
        val toolbar: MaterialToolbar = findViewById(R.id.materialToolBar)
        setSupportActionBar(toolbar)
        val assetManager = this.assets
        val folders = assetManager.list("digraphs") ?: arrayOf()
        Log.d("TEST",folders.toString())
        for (folderName in folders) {
            list.add(
                Digraph(
                    folderName.replace("_", " "),
                    "digraphs/$folderName/main/main.mp3",
                    "digraphs/$folderName/"
                )
            )
        }
        val adapter = DigraphsAdapter(this, list) { folderName ->
            val intent = Intent(this, DigraphDetailActivity::class.java)
            intent.putExtra("EXTRA_FOLDER_NAME", folderName)
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.di_graph_recy);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter




    }


}