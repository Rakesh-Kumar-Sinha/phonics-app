package com.rakesh.phonics.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rakesh.phonics.R
import com.rakesh.phonics.SoundPlayer

class GraphPagerAdapter(
    private val context: Context,
    private val type: String,
    private val folder: String,
    private val words: List<String>
) : RecyclerView.Adapter<GraphPagerAdapter.VH>() {

    // 🔴 Track currently active (red) letter / graph
    private var activeTextView: TextView? = null

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val rowLetters: LinearLayout = v.findViewById(R.id.rowLetters)
        val btn: MaterialButton = v.findViewById(R.id.btn_both_letter)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        return VH(LayoutInflater.from(p.context).inflate(R.layout.item_graph_page, p, false))
    }

    override fun onBindViewHolder(h: VH, i: Int) {

        h.rowLetters.removeAllViews()
        activeTextView = null

        val fileName = words[i]
        val word = fileName.replace(".mp3", "").lowercase()
        val graph = extractGraph(folder)

        val match = Regex(graph).find(word)
        val index = match?.range?.first ?: -1

        fun resetPrevious() {
            activeTextView?.setTextColor(Color.WHITE)
            activeTextView = null
            SoundPlayer.stop()
        }

        fun addLetter(letter: String) {
            val tv = TextView(context).apply {
                text = "$letter\n•"
                textSize = 65f
                setTextColor(Color.WHITE)
                setPadding(16, 0, 16, 0)

                setOnClickListener {
                    resetPrevious()

                    SoundPlayer.play(
                        context,
                        "alphabets/${letter.uppercase()}.mp3",
                        onStart = {
                            setTextColor(Color.RED)
                            activeTextView = this
                        },
                        onComplete = {
                            setTextColor(Color.WHITE)
                            if (activeTextView == this) activeTextView = null
                        }
                    )
                }
            }
            h.rowLetters.addView(tv)
        }

        fun addGraph(graph: String) {
            val addSub = if (graph.length == 3) "- - -" else "- -"
            val tv = TextView(context).apply {
                text = "$graph\n$addSub"
                textSize = 65f
                setTextColor(Color.YELLOW)
//                paint.isUnderlineText = true
                setPadding(16, 0, 16, 0)

                setOnClickListener {
                    resetPrevious()

                    SoundPlayer.play(
                        context,
                        "$type/$folder/main/sub.mp3",
                        onStart = {
                            setTextColor(Color.RED)
                            activeTextView = this
                        },
                        onComplete = {
                            setTextColor(Color.YELLOW)
                            if (activeTextView == this) activeTextView = null
                        }
                    )
                }
            }
            h.rowLetters.addView(tv)
        }

        if (index == -1 || graph.isEmpty()) {
            word.forEach { addLetter(it.toString()) }
        } else {
            word.substring(0, index).forEach { addLetter(it.toString()) }
            addGraph(graph)
            word.substring(index + graph.length).forEach { addLetter(it.toString()) }
        }

        h.btn.text = word.lowercase()
        h.btn.setOnClickListener {
            resetPrevious()
            SoundPlayer.play(context, "$type/$folder/$fileName")
        }
    }

    private fun extractGraph(folder: String): String {
        val afterUnderscore = folder.substringAfterLast("_").lowercase()
        val match = Regex("^[a-z]+").find(afterUnderscore)
        return match?.value ?: ""
    }

    override fun getItemCount() = words.size
}
