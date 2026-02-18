package com.rakesh.phonics.adapters

import android.content.Context
import android.graphics.Color
import android.media.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rakesh.phonics.R
import com.rakesh.phonics.models.DoubleLetter
import com.rakesh.phonics.models.TrickyWord

class TrickyWordsAdapter(
    private val context: Context,
    private val letters: ArrayList<TrickyWord>
) : RecyclerView.Adapter<TrickyWordsAdapter.TrickyViewHolder>() {

    private val vibrantColors = listOf(
        Color.parseColor("#8BC34A") // Light Green
    )

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var lastClickedHolder: TrickyViewHolder? = null

    // --- Alien prompt shown flag ---
    private var alienPromptShown = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrickyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tricky_word, parent, false)
        return TrickyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrickyViewHolder, position: Int) {
        val data = letters[position]
        holder.txtTricky.text = data.name
        holder.btnTricky.setOnClickListener {
            lastClickedHolder = holder
            holder.txtTricky.setTextColor(Color.RED)
            playAudioFromAssets(data.path, {
                holder.txtTricky.setTextColor(Color.WHITE)
            })
        }

    }

    override fun getItemCount(): Int = letters.size


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

    private fun stopAndRelease() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS
            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
        ) {
            stopAndRelease()
        }
    }

    class TrickyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTricky = itemView.findViewById<TextView>(R.id.tv_tricky_word)
        val btnTricky = itemView.findViewById<ImageView>(R.id.btn_tricky_word)

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
