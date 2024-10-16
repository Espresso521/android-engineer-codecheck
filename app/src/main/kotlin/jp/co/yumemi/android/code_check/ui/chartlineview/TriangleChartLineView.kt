package jp.co.yumemi.android.code_check.ui.chartlineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import jp.co.yumemi.android.code_check.ui.ChartLinePreviewBase

class TriangleChartLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    lineColor: Int
) : ChartLinePreviewBase(context, attrs, lineColor) {

    override fun drawChartLine(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 创建一个Path来绘制三角形
        val path = Path().apply {
            // 从中心顶部开始
            moveTo(viewWidth / 2, viewHeight / 4)
            // 画左边到左下角
            lineTo(viewWidth / 4, 3 * viewHeight / 4)
            // 画右边到右下角
            lineTo(3 * viewWidth / 4, 3 * viewHeight / 4)
            // 闭合路径，回到起点
            close()
        }

        // 使用linePaint绘制三角形
        canvas.drawPath(path, linePaint)
    }
}