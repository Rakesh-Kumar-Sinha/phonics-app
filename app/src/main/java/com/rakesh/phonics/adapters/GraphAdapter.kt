package com.rakesh.phonics.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.phonics.ActivityGraphPager
import com.rakesh.phonics.R
import com.rakesh.phonics.SoundPlayer

class GraphAdapter(
    private val context: Context,
    private val type: String,
    private val items: List<String>
) : RecyclerView.Adapter<GraphAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.di_graphs_txt)
        val speak: ImageView = v.findViewById(R.id.dia_speaker)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        return VH(LayoutInflater.from(p.context).inflate(R.layout.item_digraph, p, false))
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val folder = items[i]
        val display = folder.replace("_", " ")
        h.name.text = display

        h.speak.setOnClickListener {
            SoundPlayer.play(context, "$type/$folder/main/main.mp3")
        }

        h.itemView.setOnClickListener {
            Log.d("FOLDER",folder)
            context.startActivity(
                Intent(context, ActivityGraphPager::class.java)
                    .putExtra("TYPE", type)
                    .putExtra("FOLDER", folder)
            )
        }
    }


    override fun getItemCount() = items.size
}
