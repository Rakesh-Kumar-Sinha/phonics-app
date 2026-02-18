package com.rakesh.phonics.adapters

import android.content.Context
import android.graphics.Color
import android.media.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rakesh.phonics.R
import com.rakesh.phonics.models.DoubleLetter

class DoubleLetterAdapter(
    private val context: Context,
    private val letters: ArrayList<DoubleLetter>
) : RecyclerView.Adapter<DoubleLetterAdapter.LetterViewHolder>() {

    private val vibrantColors = listOf(
        Color.parseColor("#8BC34A") // Light Green
    )

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var lastClickedHolder: LetterViewHolder? = null

    // --- Alien prompt shown flag ---
    private var alienPromptShown = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_double_letter, parent, false)
        return LetterViewHolder(view)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val data = letters[position]

        // --- Alien static slide at position >= 12 ---
//        if (position >= 12 && !alienPromptShown) {
//            alienPromptShown = true
//
//            holder.tvLetterOne.text = "Alien"
//            holder.tvLetterTwo.text = "Words"
//            holder.tvEmoji.visibility = View.VISIBLE
//            holder.tvEmoji.text = "👽"
//            holder.btnBothLetter.text = "Continue"
//
//            holder.btnBothLetter.setOnClickListener {
//                // After continue, reload normal letters
//                notifyItemChanged(position)
//            }
//
//            return
//        }

        // --- Normal binding ---
        holder.tvLetterOne.text = data.letterOne.lowercase() + "\n•"
        holder.tvLetterTwo.text = data.letterTwo.lowercase() + "\n•"
        holder.btnBothLetter.text = data.letterOne.lowercase() + data.letterTwo.lowercase()

        if (data.bothPath.contains("ALIEN")) {
            holder.tvEmoji.visibility = View.VISIBLE
            holder.tvEmoji.text = "👽"
        } else {
            holder.tvEmoji.visibility = View.GONE
        }

        holder.tvLetterOne.setOnClickListener {
            resetAllTextColors()
            lastClickedHolder = holder
            holder.tvLetterOne.setTextColor(Color.RED)

            val path = "alphabets/" + data.letterOne.uppercase() + ".mp3"
            playAudioFromAssets(path) {
                holder.tvLetterOne.setTextColor(Color.WHITE)
            }
        }

        holder.tvLetterTwo.setOnClickListener {
            resetAllTextColors()
            lastClickedHolder = holder
            holder.tvLetterTwo.setTextColor(Color.RED)

            val path = "alphabets/" + data.letterTwo.uppercase() + ".mp3"
            playAudioFromAssets(path) {
                holder.tvLetterTwo.setTextColor(Color.WHITE)
            }
        }

        holder.btnBothLetter.setOnClickListener {
            resetAllTextColors()
            lastClickedHolder = holder
            holder.tvLetterOne.setTextColor(Color.RED)
            holder.tvLetterTwo.setTextColor(Color.RED)

            playAudioFromAssets(data.bothPath) {
                holder.tvLetterOne.setTextColor(Color.WHITE)
                holder.tvLetterTwo.setTextColor(Color.WHITE)
            }
        }

        val color = vibrantColors.random()
        holder.cardBg.setBackgroundColor(color)
    }

    override fun getItemCount(): Int = letters.size

    private fun resetAllTextColors() {
        lastClickedHolder?.let {
            it.tvLetterOne.setTextColor(Color.WHITE)
            it.tvLetterTwo.setTextColor(Color.WHITE)
        }
        lastClickedHolder = null
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
        val tvLetterOne: TextView = itemView.findViewById(R.id.tvLetterOne)
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        val tvLetterTwo: TextView = itemView.findViewById(R.id.tvLetterTwo)
        val btnBothLetter: MaterialButton = itemView.findViewById(R.id.btn_both_letter)
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
}
