package com.rakesh.phonics

import android.content.Context
import android.graphics.Color
import android.media.*
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.phonics.models.LSM

class LearnSingleLetterAdapter(
    private val context: Context,
    private val letters: ArrayList<LSM>
) : RecyclerView.Adapter<LearnSingleLetterAdapter.LetterViewHolder>() {

    private val vibrantColors = listOf(
        Color.parseColor("#8BC34A") // Light Green
    )

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // --- Track last clicked holder ---
    private var lastClickedHolder: LetterViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.letter_item_learn, parent, false)
        return LetterViewHolder(view)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val data = letters[position]
        holder.tvLetter.text = data.letter.lowercase()
        holder.tvCapLetter.text = data.letter.uppercase()

        val color = vibrantColors.random()
        holder.cardBg.setBackgroundColor(color)

        val clickListener = View.OnClickListener {
            // --- Reset previous one ---
            lastClickedHolder?.let {
                it.tvLetter.setTextColor(Color.WHITE)
                it.tvCapLetter.setTextColor(Color.WHITE)
            }

            // --- Highlight new one ---
            holder.tvLetter.setTextColor(Color.parseColor("#EA4335"))
            holder.tvCapLetter.setTextColor(Color.parseColor("#EA4335"))

            // Save this holder as last clicked
            lastClickedHolder = holder

            playAudioFromAssets(data.path) {
                // Reset only if still the last clicked one
                if (lastClickedHolder == holder) {
                    holder.tvLetter.setTextColor(Color.WHITE)
                    holder.tvCapLetter.setTextColor(Color.WHITE)
                    lastClickedHolder = null
                }
            }
            callHandler(holder,data.time,data)
//            if (holder.tvLetter.text.toString().lowercase() == "a") {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    holder.imageView.visibility = View.VISIBLE
//                    holder.imageView.setImageResource(R.drawable.red_ant)
//
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        holder.imageView.visibility = View.INVISIBLE
//                    }, 2000)
//                }, 8000)
//            }
        }

        holder.tvLetter.setOnClickListener(clickListener)
        holder.tvCapLetter.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int = letters.size
    private fun callHandler(holder: LetterViewHolder, duration: Long, data: LSM){
        Handler(Looper.getMainLooper()).postDelayed({
            holder.emojiText.visibility = View.VISIBLE
            holder.emojiText.text =data.image;
            Handler(Looper.getMainLooper()).postDelayed({
                holder.emojiText.visibility = View.INVISIBLE
            }, 2000)
        }, duration)
    }
    private fun playAudioFromAssets(fileName: String, onCompletion: () -> Unit) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            mediaPlayer = null

            val afd = context.assets.openFd(fileName)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()

                isLooping = false
                setVolume(1.0f, 1.0f)

                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    audioManager.abandonAudioFocus(audioFocusChangeListener)
                    onCompletion()
                }

                val result = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    prepare()
                    start()
                } else {
                    release()
                    mediaPlayer = null
                    onCompletion()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            stopAndRelease()
            onCompletion()
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS
            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
        ) {
            stopAndRelease()
        }
    }

    private fun stopAndRelease() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLetter: TextView = itemView.findViewById(R.id.tvLetter)
        val tvCapLetter: TextView = itemView.findViewById(R.id.tvCapLetter)
//        val imageView: ImageView = itemView.findViewById(R.id.imgLogo)
        val emojiText: TextView = itemView.findViewById(R.id.emojiText)
        val cardBg: FrameLayout = itemView.findViewById(R.id.card_bg)
    }

    fun releasePlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }
    fun isLastItem(position: Int): Boolean {
        return position == letters.size - 1
    }


}
