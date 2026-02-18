package com.rakesh.phonics

import android.content.Context
import android.media.MediaPlayer

//object SoundPlayer {
//
//    private var mediaPlayer: MediaPlayer? = null
//
//    @Synchronized
//    fun play(context: Context, assetPath: String) {
//        stop()
//
//        try {
//            val afd = context.assets.openFd(assetPath)
//            mediaPlayer = MediaPlayer().apply {
//                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//                setOnCompletionListener { stop() }
//                prepare()
//                start()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    @Synchronized
//    fun stop() {
//        mediaPlayer?.let {
//            if (it.isPlaying) {
//                it.stop()
//            }
//            it.release()
//        }
//        mediaPlayer = null
//    }
//}
object SoundPlayer {

    private var mediaPlayer: android.media.MediaPlayer? = null
    private var onComplete: (() -> Unit)? = null

    fun play(
        context: Context,
        assetPath: String,
        onStart: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        stop()
        this.onComplete = onComplete

        onStart?.invoke()

        try {
            val afd = context.assets.openFd(assetPath)
            mediaPlayer = android.media.MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener {
                    stop()
                    onComplete?.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete?.invoke()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
