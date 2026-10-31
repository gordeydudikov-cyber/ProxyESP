package com.example.proxiesp

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var demo: DemoView
    private lateinit var espButton: Button
    private lateinit var whButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 24, 18, 18)
            setBackgroundColor(Color.rgb(13, 18, 28))
        }

        val title = TextView(this).apply {
            text = "PROXY ESP  •  DEMO"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Тестовая сцена — не подключается к Standoff 2"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 14)
        }
        root.addView(subtitle)

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        espButton = Button(this).apply { setOnClickListener {
            demo.espEnabled = !demo.espEnabled
            refreshButtons()
            demo.invalidate()
        } }
        whButton = Button(this).apply { setOnClickListener {
            demo.whEnabled = !demo.whEnabled
            refreshButtons()
            demo.invalidate()
        } }
        controls.addView(espButton, LinearLayout.LayoutParams(0, 52, 1f))
        controls.addView(whButton, LinearLayout.LayoutParams(0, 52, 1f))
        root.addView(controls)

        demo = DemoView(this)
        root.addView(demo, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val note = TextView(this).apply {
            text = "ESP обводит манекены. WH показывает их контуры сквозь стену в этой демонстрационной сцене."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 4)
        }
        root.addView(note)
        setContentView(root)
        refreshButtons()
    }

    private fun refreshButtons() {
        espButton.text = if (demo.espEnabled) "ESP: ON" else "ESP: OFF"
        whButton.text = if (demo.whEnabled) "WH: ON" else "WH: OFF"
    }
}

private class DemoView(context: android.content.Context) : View(context) {
    var espEnabled = true
    var whEnabled = true

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(25, 34, 47))
        val w = width.toFloat()
        val h = height.toFloat()

        // Grid / floor
        stroke.color = Color.rgb(52, 68, 86)
        stroke.strokeWidth = 1.5f
        for (i in 1..8) {
            val y = h * i / 9f
            canvas.drawLine(0f, y, w, y, stroke)
        }

        val targets = listOf(
            RectF(w * .18f, h * .22f, w * .34f, h * .60f),
            RectF(w * .62f, h * .17f, w * .79f, h * .56f)
        )

        // Draw target mannequins first.
        targets.forEachIndexed { index, r ->
            fill.color = Color.rgb(210, 92, 78)
            canvas.drawCircle(r.centerX(), r.top + r.width() * .28f, r.width() * .22f, fill)
            canvas.drawRoundRect(
                RectF(r.left + r.width() * .22f, r.top + r.width() * .52f,
                    r.right - r.width() * .22f, r.bottom), 14f, 14f, fill
            )
            if (espEnabled) {
                stroke.color = Color.rgb(65, 235, 135)
                stroke.strokeWidth = 4f
                canvas.drawRect(r, stroke)
                text.color = Color.rgb(110, 255, 170)
                text.textSize = 24f
                canvas.drawText("TARGET ${index + 1}", r.left - 8f, r.top - 12f, text)
            }
        }

        // Opaque obstacle in front of the second target.
        fill.color = Color.rgb(92, 104, 121)
        val wall = RectF(w * .48f, h * .30f, w * .90f, h * .48f)
        canvas.drawRoundRect(wall, 10f, 10f, fill)
        text.color = Color.WHITE
        text.textSize = 22f
        canvas.drawText("WALL", wall.left + 18f, wall.centerY() + 8f, text)

        // Demonstration-only x-ray outline drawn over the wall.
        if (whEnabled) {
            val r = targets[1]
            stroke.color = Color.rgb(255, 205, 65)
            stroke.strokeWidth = 5f
            canvas.drawRect(r, stroke)
            fill.color = Color.argb(65, 255, 205, 65)
            canvas.drawRect(r, fill)
            text.color = Color.rgb(255, 220, 100)
            text.textSize = 22f
            canvas.drawText("VISIBLE THROUGH WALL", 20f, h - 28f, text)
        }
    }
}
