package com.rakesh.phonics.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rakesh.phonics.R
import com.rakesh.phonics.models.Digraph

class DigraphsAdapter(
    private val context: Context,
    private val letters: ArrayList<Digraph>,
    private val onItemClick: (folderName: String) -> Unit
) : RecyclerView.Adapter<DigraphsAdapter.DigraphsViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Track which item is currently playing. -1 means none.
    private var currentlyPlayingPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DigraphsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_digraph, parent, false)
        return DigraphsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DigraphsViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = letters[position]

        // Always bind text and initial icon based on playing state
        holder.textDigraph.text = item.text
        if (position == currentlyPlayingPosition) {
            holder.btnDigraph.setImageResource(R.drawable.speaker_red)
        } else {
            holder.btnDigraph.setImageResource(R.drawable.speakerx)
        }

        holder.btnDigraph.setOnClickListener {
            // If same item clicked while playing -> stop
            if (position == currentlyPlayingPosition) {
                stopAndRelease()
                val old = currentlyPlayingPosition
                currentlyPlayingPosition = -1
                if (old >= 0) notifyItemChanged(old)
                return@setOnClickListener
            }

            // Start new audio: update UI for previous and current
            val previous = currentlyPlayingPosition
            currentlyPlayingPosition = position
            if (previous >= 0) notifyItemChanged(previous)
            notifyItemChanged(position) // will set icon to playing immediately

            playAudioFromAssets(item.path) {
                // playback finished -> clear playing state and update UI
                val finishedPos = currentlyPlayingPosition
                currentlyPlayingPosition = -1
                if (finishedPos >= 0) notifyItemChanged(finishedPos)
            }
        }
        holder.itemView.setOnClickListener {
            // Derive folder name from the asset path.
            // Example path: "digraphs/ch/main/main.mp3" -> folderName = "ch"
            val pathParts = item.path.split("/")
//            val folderName = if (pathParts.size >= 2) pathParts[1] else item.text.replace(" ", "_")
            val folderName = item.detailPath
            onItemClick(folderName)
        }
    }

    override fun getItemCount(): Int = letters.size

    override fun onViewRecycled(holder: DigraphsViewHolder) {
        super.onViewRecycled(holder)
        // No per-view resources to free here because we control playback globally
        // but we ensure the view shows correct icon in onBindViewHolder
    }

    class DigraphsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDigraph: TextView = itemView.findViewById(R.id.di_graphs_txt)
        val btnDigraph: ImageView = itemView.findViewById(R.id.dia_speaker)
    }

    private fun playAudioFromAssets(fileName: String, onCompletion: () -> Unit) {
        try {
            // stop any existing player first
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
                    // Could not get audio focus
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
            // update UI: clear currentlyPlayingPosition and notify the changed item
            val old = currentlyPlayingPosition
            currentlyPlayingPosition = -1
            if (old >= 0) notifyItemChanged(old)
        }
    }

    private fun stopAndRelease() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        try {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        } catch (_: Exception) { /* ignore */ }
    }

    /**
     * Call this from the Activity/Fragment's onDestroy() or onStop() as appropriate
     * to free native resources.
     */
    fun releasePlayer() {
        stopAndRelease()
        // clear playing state UI
        val old = currentlyPlayingPosition
        currentlyPlayingPosition = -1
        if (old >= 0) notifyItemChanged(old)
    }
}
