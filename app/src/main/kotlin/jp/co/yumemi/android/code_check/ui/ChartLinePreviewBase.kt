package jp.co.yumemi.android.code_check.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

abstract class ChartLinePreviewBase @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private var lineColor: Int
) : View(context, attrs) {

    private val gridLineLength = 10f
    private val gridSize = 100f

    protected val linePaint = Paint().apply {
        color = lineColor
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val gridPaint = Paint().apply {
        color = 0xFF888888.toInt()
        strokeWidth = 1f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(gridLineLength, gridLineLength), 0f)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        drawGridLines(canvas, viewWidth, viewHeight)
        
        drawChartLine(canvas)
    }

    abstract fun drawChartLine(canvas: Canvas)

    private fun drawGridLines(canvas: Canvas, width: Float, height: Float) {
        var x = 0f
        while (x <= width) {
            canvas.drawLine(x, 0f, x, height, gridPaint)
            x += gridSize
        }

        var y = 0f
        while (y <= height) {
            canvas.drawLine(0f, y, width, y, gridPaint)
            y += gridSize
        }
    }

    open fun changeColor(newColor: Int) {
        linePaint.color = newColor
        invalidate()
    }
}

