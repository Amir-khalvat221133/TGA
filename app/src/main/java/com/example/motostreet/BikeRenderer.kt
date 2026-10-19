package com.example.motostreet

import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin

fun col(v: Long): Int = v.toInt()

/**
 * 2D cartoon redraw of the real bike photo (Honda CG125-style): yellow tank with grey/black
 * stripe, yellow side cover, long black seat + chrome rear rack, round chrome headlight,
 * spoked wheels, chrome front fender, silver engine, orange rear shocks, chrome exhaust.
 * Local coords: origin = rear axle, +x forward, -y up, wheelbase 120, wheel radius ~24.
 */
class BikeRenderer {
    private val OUT = col(0xFF22201F)
    private val YELLOW = col(0xFFF2C200)
    private val CHROME = col(0xFFD5DADF)
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = OUT; strokeWidth = 3f
        strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND
    }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND
    }
    private val path = Path()
    private val rect = RectF()

    private fun shape(c: Canvas, color: Int, build: Path.() -> Unit) {
        path.reset(); path.build()
        fill.color = color; c.drawPath(path, fill); c.drawPath(path, line)
    }

    private fun bar(c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, w: Float, color: Int) {
        stroke.color = OUT; stroke.strokeWidth = w + 3.5f; c.drawLine(x1, y1, x2, y2, stroke)
        stroke.color = color; stroke.strokeWidth = w; c.drawLine(x1, y1, x2, y2, stroke)
    }

    private fun arc(c: Canvas, cx: Float, cy: Float, r: Float, s: Float, sw: Float, w: Float, color: Int) {
        rect.set(cx - r, cy - r, cx + r, cy + r)
        stroke.color = OUT; stroke.strokeWidth = w + 3.5f; c.drawArc(rect, s, sw, false, stroke)
        stroke.color = color; stroke.strokeWidth = w; c.drawArc(rect, s, sw, false, stroke)
    }

    private fun circle(c: Canvas, x: Float, y: Float, r: Float, color: Int) {
        fill.color = color; c.drawCircle(x, y, r, fill); c.drawCircle(x, y, r, line)
    }

    private fun wheel(c: Canvas, x: Float, y: Float, deg: Float) {
        stroke.color = col(0xFF1E1E22); stroke.strokeWidth = 8f; c.drawCircle(x, y, 24f, stroke)
        stroke.color = CHROME; stroke.strokeWidth = 2.5f; c.drawCircle(x, y, 19.5f, stroke)
        stroke.color = col(0xFF9AA0A6); stroke.strokeWidth = 1.2f
        for (i in 0 until 12) {
            val a = Math.toRadians((deg + i * 30f).toDouble())
            c.drawLine(x, y, x + 19f * cos(a).toFloat(), y + 19f * sin(a).toFloat(), stroke)
        }
        circle(c, x, y, 5f, CHROME)
    }

    fun draw(c: Canvas, wheelDeg: Float) {
        // rear: fender, shock, swingarm, wheel
        wheel(c, 0f, 0f, wheelDeg)
        arc(c, 0f, 0f, 31f, 200f, 140f, 5f, col(0xFF2A2A2E))
        bar(c, 0f, 0f, 36f, -18f, 5f, col(0xFF4A4F55))
        bar(c, -12f, -50f, 2f, -8f, 5.5f, col(0xFFE8851A))
        // exhaust (chrome muffler)
        bar(c, 84f, -12f, 12f, -13f, 4f, CHROME)
        bar(c, 12f, -13f, -22f, -20f, 8f, CHROME)
        // engine block
        shape(c, col(0xFFB5BBC2)) { moveTo(34f, -36f); lineTo(80f, -36f); lineTo(86f, -12f); lineTo(30f, -10f); close() }
        shape(c, col(0xFF2B2B30)) { moveTo(44f, -52f); lineTo(66f, -54f); lineTo(68f, -34f); lineTo(44f, -34f); close() }
        stroke.color = col(0xFF6B7078); stroke.strokeWidth = 1.5f
        for (i in 0 until 3) c.drawLine(45f, -48f + i * 6f, 67f, -50f + i * 6f, stroke)
        circle(c, 66f, -22f, 9f, col(0xFFD9DEE3))
        // seat + rack + side cover
        shape(c, col(0xFF1B1B1F)) {
            moveTo(-24f, -60f); lineTo(40f, -62f); quadTo(48f, -61f, 44f, -54f)
            lineTo(-22f, -50f); quadTo(-31f, -52f, -24f, -60f); close()
        }
        bar(c, -36f, -56f, -14f, -63f, 3f, CHROME)
        bar(c, -36f, -56f, -22f, -44f, 3f, CHROME)
        shape(c, YELLOW) { moveTo(4f, -50f); lineTo(40f, -49f); lineTo(42f, -28f); lineTo(8f, -30f); close() }
        // tank + stripe
        shape(c, YELLOW) {
            moveTo(36f, -60f); cubicTo(40f, -77f, 70f, -81f, 92f, -70f)
            lineTo(88f, -56f); cubicTo(70f, -52f, 52f, -50f, 36f, -60f); close()
        }
        shape(c, col(0xFF555B63)) { moveTo(48f, -63f); lineTo(86f, -68f); lineTo(84f, -61f); lineTo(52f, -56f); close() }
        rider(c)
        // front end
        bar(c, 120f, 0f, 98f, -66f, 4f, CHROME)
        wheel(c, 120f, 0f, wheelDeg)
        arc(c, 120f, 0f, 29f, 215f, 110f, 4f, CHROME)
        circle(c, 96f, -73f, 11f, CHROME)
        circle(c, 104f, -73f, 6f, col(0xFFFFF3B0))
        circle(c, 86f, -84f, 7f, col(0xFF1B1B1F))
        bar(c, 92f, -80f, 74f, -90f, 4.5f, col(0xFF1B1B1F))
        fill.color = col(0xFFE0A87C); c.drawCircle(77f, -89f, 5f, fill)
    }

    private fun rider(c: Canvas) {
        val shirt = col(0xFFE8821E); val pants = col(0xFF6E8F3A); val hair = col(0xFF3A2418)
        bar(c, 14f, -66f, 52f, -50f, 10f, pants)
        bar(c, 52f, -50f, 40f, -18f, 9f, pants)
        shape(c, col(0xFFF4F4F0)) { addOval(RectF(30f, -21f, 56f, -10f), Path.Direction.CW) }
        shape(c, shirt) { moveTo(4f, -68f); lineTo(22f, -70f); lineTo(40f, -110f); lineTo(24f, -114f); close() }
        bar(c, 34f, -106f, 76f, -88f, 7f, shirt)
        circle(c, 36f, -132f, 15f, hair)
        circle(c, 46f, -124f, 10f, col(0xFFE0A87C))
        bar(c, 48f, -120f, 58f, -121f, 3f, hair)
        fill.color = OUT; c.drawCircle(50f, -127f, 1.6f, fill)
    }
}
