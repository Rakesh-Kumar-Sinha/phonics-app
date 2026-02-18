package com.rakesh.phonics.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.phonics.R
import com.rakesh.phonics.adapters.DigraphsAdapter.DigraphsViewHolder
import com.rakesh.phonics.adapters.FourLetterAdapter.LetterViewHolder
import com.rakesh.phonics.models.Digraph

class DigraphsDetailAdapter(
    private val context: Context,
    private val letters: ArrayList<Digraph>,
) : RecyclerView.Adapter<DigraphsDetailAdapter.DigraphsDetailViewHolder>() {
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var lastClickedHolder: DigraphsDetailViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DigraphsDetailAdapter.DigraphsDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_digraph_detail, parent, false)
        return DigraphsDetailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DigraphsDetailAdapter.DigraphsDetailViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = letters[position]
        Log.d("TEST",item.toString())
        holder.textDigraph.text = item.text
        val path = item.path.removePrefix("/")
//        val path = "digraphs/digraph_ar/bark.mp3"
        holder.textDigraph.setOnClickListener {
            lastClickedHolder = holder
            resetAllTextColors()
            holder.textDigraph.setTextColor(Color.RED)
            playAudioFromAssets(path) {
                holder.textDigraph.setTextColor(Color.WHITE)
            }
        }
//        holder.tvLetterFour.setOnClickListener {
//
//
//            val path = "alphabets/" + data.letterFour.uppercase() + ".mp3"
//
//        }
    }

    override fun getItemCount(): Int {
        return letters.size
    }


    class DigraphsDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDigraph: TextView = itemView.findViewById(R.id.tvDigraphDetail)
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
    private fun resetAllTextColors() {
        lastClickedHolder?.let {
            it.textDigraph.setTextColor(Color.WHITE)

        }
        lastClickedHolder = null
    }
    private fun stopAndRelease() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

}