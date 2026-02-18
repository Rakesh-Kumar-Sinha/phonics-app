package com.rakesh.phonics.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.phonics.R
import com.rakesh.phonics.SoundPlayer
import com.rakesh.phonics.helpers.ArcView
import com.rakesh.phonics.models.SplitDigraphItem

class SplitDigraphPagerAdapter(
    private val context: Context,
    private val items: List<SplitDigraphItem>
) : RecyclerView.Adapter<SplitDigraphPagerAdapter.VH>() {

    private val activeSplitViews = mutableListOf<TextView>()
    private var activeSingleView: TextView? = null

    inner class VH(val container: FrameLayout) : RecyclerView.ViewHolder(container) {
        val row: LinearLayout = container.findViewById(R.id.rowLetters)
        val wordBtn: TextView = container.findViewById(R.id.wordBtn)
        var arcView: ArcView? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_split_digraph_page, parent, false) as FrameLayout
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        // Clean up previous state
        holder.row.removeAllViews()
        holder.arcView?.let { holder.container.removeView(it) }
        holder.arcView = null

        activeSplitViews.clear()
        activeSingleView = null
        SoundPlayer.stop()

        val letterViews = mutableMapOf<Int, TextView>()

        // ---------- RESET FUNCTION ----------
        fun resetActive() {
            activeSingleView?.setTextColor(Color.WHITE)
            activeSingleView = null

            activeSplitViews.forEach {
                it.setTextColor(Color.YELLOW)
            }
            activeSplitViews.clear()

            SoundPlayer.stop()
        }

        // ---------- WORD BUTTON ----------
        holder.wordBtn.text = item.word
        holder.wordBtn.setOnClickListener {
            resetActive()
            SoundPlayer.play(
                context,
                item.wordSound,
                onComplete = {
                    resetActive()
                }
            )
        }

        // ---------- LETTERS ----------
        item.word.forEachIndexed { index, c ->
            val isSplit = item.indices.contains(index)
            val tv = TextView(context).apply {
                tag = index
                text = if (isSplit) {
                    c.toString()+"\n"
                } else {
                    "${c}\n•"
                }
                textSize = 60f
                setPadding(12, 16, 12, 16)
                setTextColor(if (isSplit) Color.YELLOW else Color.WHITE)
                gravity = android.view.Gravity.CENTER
                minWidth = 100
            }

            letterViews[index] = tv

            tv.setOnClickListener {
                resetActive()

                if (isSplit) {
                    // ✅ BOTH split letters turn RED
                    item.indices.forEach { i ->
                        letterViews[i]?.let { view ->
                            view.setTextColor(Color.RED)
                            activeSplitViews.add(view)
                        }
                    }

                    SoundPlayer.play(
                        context,
                        item.digraphSound,
                        onComplete = {
                            activeSplitViews.forEach { view ->
                                view.setTextColor(Color.YELLOW)
                            }
                            activeSplitViews.clear()
                        }
                    )

                } else {
                    // ✅ SINGLE letter logic
                    tv.setTextColor(Color.RED)
                    activeSingleView = tv

                    SoundPlayer.play(
                        context,
                        "alphabets/${c.uppercase()}.mp3",
                        onComplete = {
                            tv.setTextColor(Color.WHITE)
                            if (activeSingleView == tv) activeSingleView = null
                        }
                    )
                }
            }

            holder.row.addView(tv)
        }

        // ---------- ARC ----------
        drawArc(holder, item.indices)
    }

    // ================= ARC DRAWING =================
    private fun drawArc(holder: VH, indices: List<Int>) {
        if (indices.size != 2) return

        holder.row.post {
            val startView = holder.row.getChildAt(indices[0])
            val endView = holder.row.getChildAt(indices[1])
            if (startView == null || endView == null) return@post

            val arcHeight = 160

            val startLoc = IntArray(2)
            val endLoc = IntArray(2)
            val rootLoc = IntArray(2)

            startView.getLocationOnScreen(startLoc)
            endView.getLocationOnScreen(endLoc)
            holder.container.getLocationOnScreen(rootLoc)

            val startX = startLoc[0] - rootLoc[0] + startView.width / 2f
            val endX = endLoc[0] - rootLoc[0] + endView.width / 2f

            val arcView = ArcView(context).apply {
                this.startX = startX
                this.endX = endX
            }

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                arcHeight
            )

            params.topMargin = holder.row.top - arcHeight
            holder.container.addView(arcView, params)
            holder.arcView = arcView
        }
    }

    override fun getItemCount() = items.size

    // Clean up when recycling
    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.arcView?.let { holder.container.removeView(it) }
        holder.arcView = null
        SoundPlayer.stop()
    }
}