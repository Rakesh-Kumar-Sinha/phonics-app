package com.rakesh.phonics.helpers.FinalPopupHelper;

import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import com.rakesh.phonics.R

object FinalPopupHelper {

    private var dialog: Dialog? = null

    fun show(activity: Activity, goBackOnClick: Boolean = true) {

        if (dialog?.isShowing == true) return

        dialog = Dialog(activity)

        dialog?.setContentView(R.layout.popup_final)

        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)

        dialog?.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setGravity(Gravity.CENTER)

        val btnGoBack = dialog?.findViewById<Button>(R.id.btnGoBack)

        btnGoBack?.setOnClickListener {

            dialog?.dismiss()

            if (goBackOnClick) {
                activity.finish()
            }
        }

        dialog?.show()

        playRandomFinalSound(activity)
    }

    private fun playRandomFinalSound(activity: Activity) {

        try {

            val files = activity.assets.list("final") ?: return

            if (files.isEmpty()) return

            val randomFile = files.random()

            val afd = activity.assets.openFd("final/$randomFile")

            val player = MediaPlayer()

            player.setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            )

            afd.close()

            player.prepare()
            player.start()

            player.setOnCompletionListener {
                it.release()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
