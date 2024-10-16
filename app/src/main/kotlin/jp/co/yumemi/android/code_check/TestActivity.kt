/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.ActivityTestBinding
import jp.co.yumemi.android.code_check.ui.FibonacciHorizontalLinesView
import jp.co.yumemi.android.code_check.ui.RectangleChartView

@AndroidEntryPoint
class TestActivity : AppCompatActivity(R.layout.activity_test) {

    private lateinit var binding: ActivityTestBinding

    companion object {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 创建自定义的 CustomShapeView
        //val customShapeView = TriangleChartLineView(this, null, Color.BLUE)
        val customShapeView = FibonacciHorizontalLinesView(this, null, Color.BLUE)
        binding.selfView.addView(customShapeView)

        // 按钮点击事件，改变颜色
        binding.changeColor.setOnClickListener {
            customShapeView.changeColor(Color.RED)  // 假设有一个方法可以改变颜色
        }
    }
}
