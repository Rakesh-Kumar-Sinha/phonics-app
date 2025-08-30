package com.rakesh.phonics

import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
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
//        Color.parseColor("#FFB300"), // Amber
//        Color.parseColor("#34A853"), // Green
//        Color.parseColor("#FBBC05"), // Yellow
//        Color.parseColor("#AB47BC"), // Purple
        Color.parseColor("#8BC34A"), // Light Green
//        Color.parseColor("#FFD54F"), // Soft Amber
//        Color.parseColor("#A1887F"), // Warm Brown
//        Color.parseColor("#F4511E"), // Deep Orange (more earthy, not pure red)
//        Color.parseColor("#9E9D24")  // Olive

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
            val afd = context.assets.openFd(fileName)
            val mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                isLooping = false
                setVolume(1.0f, 1.0f) // make sure no channel imbalance

                setOnCompletionListener {
                    it.release()
                    onCompletion()
                }

                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion()
        }
    }




    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLetter: TextView = itemView.findViewById(R.id.tvLetter)

        //        val btnSpeaker: ImageView = itemView.findViewById(R.id.btnSpeaker)
        val cardBg: FrameLayout = itemView.findViewById(R.id.card_bg)
    }
}
