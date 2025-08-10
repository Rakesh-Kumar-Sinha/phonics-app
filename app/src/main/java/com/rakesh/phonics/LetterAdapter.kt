package com.rakesh.phonics

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LetterAdapter(
    private val context: Context,
    private val letters: List<LetterData>
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    private val vibrantColors = listOf(
        Color.parseColor("#FFB300"), // Amber
        Color.parseColor("#34A853"), // Green
        Color.parseColor("#FBBC05"), // Yellow
        Color.parseColor("#AB47BC"), // Purple
        Color.parseColor("#8BC34A"), // Light Green
        Color.parseColor("#FFD54F"), // Soft Amber
        Color.parseColor("#A1887F"), // Warm Brown
        Color.parseColor("#F4511E"), // Deep Orange (more earthy, not pure red)
        Color.parseColor("#9E9D24")  // Olive

    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter, parent, false)
        return LetterViewHolder(view)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val data = letters[position]
        holder.tvLetter.text = data.letter

        val color = vibrantColors.random()
        holder.cardBg.setBackgroundColor(color)

        holder.tvLetter.setOnClickListener {
            holder.tvLetter.setTextColor(Color.parseColor("#EA4335")) // Set to red
            playAudioFromAssets(data.audioPath) {
                holder.tvLetter.setTextColor(Color.WHITE) // Reset to white after playback
            }
        }

    }

    override fun getItemCount(): Int = letters.size

    private fun playAudioFromAssets(fileName: String, onCompletion: () -> Unit) {
        try {
            val assetFileDescriptor = context.assets.openFd(fileName)
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            assetFileDescriptor.close()

            mediaPlayer.setOnPreparedListener { it.start() }

            mediaPlayer.setOnCompletionListener {
                it.release()
                onCompletion() // Call the callback after playback
            }

            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion() // Ensure callback is called even on error
        }
    }


    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLetter: TextView = itemView.findViewById(R.id.tvLetter)

        //        val btnSpeaker: ImageView = itemView.findViewById(R.id.btnSpeaker)
        val cardBg: FrameLayout = itemView.findViewById(R.id.card_bg)
    }
}
