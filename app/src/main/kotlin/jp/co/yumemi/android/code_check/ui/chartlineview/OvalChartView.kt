package jp.co.yumemi.android.code_check.ui.chartlineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import jp.co.yumemi.android.code_check.ui.ChartLinePreviewBase

class OvalChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 定义椭圆的边界矩形
        val ovalRect = RectF(
            viewWidth * 0.2f,  // 左侧偏移量
            viewHeight * 0.3f, // 上侧偏移量
            viewWidth * 0.8f,  // 右侧偏移量
            viewHeight * 0.7f  // 下侧偏移量
        )

        // 绘制椭圆
        canvas.drawOval(ovalRect, linePaint)
    }
}