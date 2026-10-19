package com.example.motostreet

import android.graphics.*
import java.util.Random
import kotlin.math.floor

/** Side-view Tehran-style alley: parallax brick buildings, sidewalk, two-lane road, grass, cars. */
class StreetRenderer {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val o = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = col(0xFF22201F); strokeWidth = 2.5f; strokeJoin = Paint.Join.ROUND
    }
    private val path = Path()
    private val palette = longArrayOf(0xFFC9A66B, 0xFF9C7A48, 0xFFB5543A, 0xFFA39B7A, 0xFF7C8C6E, 0xFF8A5A44)
        .map { col(it) }.toIntArray()
    private val carColors = intArrayOf(col(0xFF9A5A2C), col(0xFF4F6D8C), col(0xFFC9C4B8))

    fun groundY(h: Float) = h * 0.74f // bike lane contact line

    fun drawBackground(c: Canvas, camX: Float, w: Float, h: Float, sc: Float) {
        val sidewalkTop = h * 0.50f; val roadTop = h * 0.60f; val grassTop = h * 0.93f
        p.color = col(0xFF8FA6C8); c.drawRect(0f, 0f, w, sidewalkTop, p)
        val bw = 250f; val off = camX * 0.75f
        val first = floor(off / bw).toInt()
        for (i in first until first + (w / sc / bw).toInt() + 3)
            building(c, i, (i * bw - off) * sc, sidewalkTop, bw * sc, sc)
        p.color = col(0xFFB09A86); c.drawRect(0f, sidewalkTop, w, roadTop, p)
        p.color = col(0xFF8C7A6A)
        val tile = 70f
        for (k in floor(camX / tile).toInt()..floor(camX / tile).toInt() + (w / sc / tile).toInt() + 2)
            c.drawRect((k * tile - camX) * sc, sidewalkTop, (k * tile - camX) * sc + 3f, roadTop, p)
        p.color = col(0xFF6F7378); c.drawRect(0f, roadTop, w, grassTop, p)
        p.color = col(0xFFE6E2D6); c.drawRect(0f, roadTop, w, roadTop + 5f, p)
        val dash = 140f; val ly = h * 0.78f
        for (k in floor(camX / dash).toInt()..floor(camX / dash).toInt() + (w / sc / dash).toInt() + 2)
            c.drawRect((k * dash - camX) * sc, ly, (k * dash + 70f - camX) * sc, ly + 5f, p)
        p.color = col(0xFF5E8A3C); c.drawRect(0f, grassTop, w, h, p)
    }

    private fun building(c: Canvas, i: Int, x: Float, base: Float, bw: Float, sc: Float) {
        val r = Random(i * 7919L + 13)
        val bh = (130 + r.nextInt(80)) * sc; val top = base - bh
        p.color = palette[r.nextInt(palette.size)]; c.drawRect(x, top, x + bw, base, p); c.drawRect(x, top, x + bw, base, o)
        p.color = col(0x33000000); c.drawRect(x, top, x + bw, top + 10 * sc, p)
        var y = top + 22 * sc
        while (y + 38 * sc < base - 60 * sc) {
            for (cI in 0 until 3) {
                val wx = x + (24 + cI * 74) * sc
                p.color = col(0xFF3B4150); c.drawRect(wx, y, wx + 34 * sc, y + 38 * sc, p)
                c.drawRect(wx, y, wx + 34 * sc, y + 38 * sc, o)
            }
            if (r.nextBoolean()) { p.color = col(0xFFD8D4C8); c.drawRect(x + 20 * sc, y + 40 * sc, x + 120 * sc, y + 46 * sc, p) }
            y += 64 * sc
        }
        p.color = if (r.nextBoolean()) col(0xFF3E7A5A) else col(0xFF7A3E3E)
        c.drawRect(x + 150 * sc, base - 58 * sc, x + 230 * sc, base, p); c.drawRect(x + 150 * sc, base - 58 * sc, x + 230 * sc, base, o)
    }

    fun drawCars(c: Canvas, camX: Float, w: Float, h: Float, sc: Float) {
        val gap = 1000f
        val first = floor((camX - 250f) / gap).toInt()
        for (j in first..first + (w / sc / gap).toInt() + 2) {
            val r = Random(j * 31L + 7)
            val wx = j * gap + r.nextInt(400)
            car(c, (wx - camX) * sc, h * 0.91f, sc, carColors[r.nextInt(carColors.size)])
        }
    }

    private fun car(c: Canvas, sx: Float, gy: Float, sc: Float, color: Int) {
        c.save(); c.translate(sx, gy); c.scale(sc, sc)
        fun poly(fc: Int, b: Path.() -> Unit) { path.reset(); path.b(); p.color = fc; c.drawPath(path, p); c.drawPath(path, o) }
        poly(color) {
            moveTo(0f, -20f); lineTo(0f, -45f); lineTo(40f, -50f); lineTo(70f, -80f)
            lineTo(150f, -80f); lineTo(180f, -50f); lineTo(200f, -45f); lineTo(200f, -20f); close()
        }
        val glass = col(0xFF262B33)
        poly(glass) { moveTo(72f, -76f); lineTo(100f, -76f); lineTo(100f, -52f); lineTo(50f, -52f); close() }
        poly(glass) { moveTo(106f, -76f); lineTo(148f, -76f); lineTo(172f, -52f); lineTo(106f, -52f); close() }
        for (wx in floatArrayOf(45f, 160f)) {
            p.color = col(0xFF1E1E22); c.drawCircle(wx, -18f, 20f, p)
            p.color = col(0xFFB8BEC4); c.drawCircle(wx, -18f, 9f, p)
        }
        c.restore()
    }
}
