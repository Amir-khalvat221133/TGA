package com.example.motostreet

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.min

/** Game loop + touch controls + camera. Buttons (right->left): gas, wheelie, brake. */
class GameView(ctx: Context) : View(ctx) {
    private val bike = BikeState()
    private val bikeR = BikeRenderer()
    private val street = StreetRenderer()
    private val down = BooleanArray(3) // 0 gas, 1 wheelie, 2 brake
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.RIGHT
    }
    private val textOut = Paint(text).apply { style = Paint.Style.STROKE; color = Color.BLACK; strokeWidth = 6f }
    private var camX = 0f
    private var camInit = false
    private var last = 0L

    private fun sc() = height / 340f
    private fun btnR() = height * 0.085f
    private fun btnX(k: Int) = width - btnR() * 1.4f - k * btnR() * 2.5f
    private fun btnY() = height - btnR() * 1.5f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        down.fill(false)
        val ending = e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL
        if (!ending) for (i in 0 until e.pointerCount) {
            if (e.actionMasked == MotionEvent.ACTION_POINTER_UP && i == e.actionIndex) continue
            for (k in 0..2) if (hypot(e.getX(i) - btnX(k), e.getY(i) - btnY()) < btnR() * 1.4f) down[k] = true
        }
        return true
    }

    override fun onDraw(c: Canvas) {
        val now = System.nanoTime()
        val dt = if (last == 0L) 0.016f else min((now - last) / 1e9f, 0.05f)
        last = now
        val w = width.toFloat(); val h = height.toFloat(); val s = sc()

        bike.update(dt, down[0], down[2], down[1])

        val bikeScreenX = w * 0.28f
        val target = bike.x - bikeScreenX / s + bike.speed * 0.12f // look-ahead
        if (!camInit) { camX = target; camInit = true }
        camX += (target - camX) * min(1f, dt * 6f)

        street.drawBackground(c, camX, w, h, s)

        c.save()
        c.translate((bike.x - camX) * s, street.groundY(h) - 28f * s)
        c.scale(s, s)
        c.rotate(-bike.tilt, 0f, 28f) // pivot around rear tyre contact
        bikeR.draw(c, bike.wheelDeg)
        c.restore()

        street.drawCars(c, camX, w, h, s)
        drawHud(c, w, h)
        postInvalidateOnAnimation()
    }

    private fun persian(n: Int) = n.toString().map { '۰' + (it - '0') }.joinToString("")

    private fun drawHud(c: Canvas, w: Float, h: Float) {
        text.textSize = h * 0.075f; textOut.textSize = text.textSize
        val t = "${persian((bike.x / 40f).toInt())} متر"
        c.drawText(t, w - 24f, h * 0.10f, textOut); c.drawText(t, w - 24f, h * 0.10f, text)
        val r = btnR()
        for (k in 0..2) {
            val x = btnX(k); val y = btnY()
            p.style = Paint.Style.FILL; p.color = if (down[k]) col(0xFFFF5A4D) else col(0xFFC62828); c.drawCircle(x, y, r, p)
            p.style = Paint.Style.STROKE; p.strokeWidth = 5f; p.color = col(0xFF5B1010); c.drawCircle(x, y, r, p)
            p.style = Paint.Style.FILL; p.color = Color.BLACK
            when (k) {
                0 -> { val a = Path().apply { moveTo(x - r * .3f, y - r * .45f); lineTo(x + r * .5f, y); lineTo(x - r * .3f, y + r * .45f); close() }; c.drawPath(a, p) }
                2 -> { c.drawRect(x - r * .4f, y - r * .4f, x - r * .1f, y + r * .4f, p); c.drawRect(x + r * .1f, y - r * .4f, x + r * .4f, y + r * .4f, p) }
                else -> { c.drawCircle(x - r * .35f, y + r * .25f, r * .22f, p); c.drawCircle(x + r * .4f, y - r * .1f, r * .22f, p)
                    p.style = Paint.Style.STROKE; p.strokeWidth = 6f; c.drawLine(x - r * .35f, y + r * .25f, x + r * .4f, y - r * .1f, p) }
            }
        }
    }
}
