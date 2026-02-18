package com.rakesh.phonics.helpers

import android.content.Context
import android.graphics.*
import android.view.View
class ArcView(context: Context) : View(context) {

    var startX = 0f
    var endX = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        val midX = (startX + endX) / 2f
        val arcHeight = height * 0.85f

        path.moveTo(startX, height.toFloat())
        path.quadTo(midX, height - arcHeight, endX, height.toFloat())

        canvas.drawPath(path, paint)
    }
}

