package com.example.motostreet

import kotlin.math.min

/** Bike physics in world units (1 unit = 1 bike-design unit; wheelbase = 120). */
class BikeState {
    var x = 0f
    var speed = 0f
    var wheelDeg = 0f
    var tilt = 0f // wheelie angle in degrees

    fun update(dt: Float, gas: Boolean, brake: Boolean, wheelie: Boolean) {
        if (gas) speed += 320f * dt
        if (brake) speed -= 800f * dt
        speed -= 90f * dt
        speed = speed.coerceIn(0f, 650f)
        x += speed * dt
        wheelDeg += speed * dt / 24f * 57.3f
        val target = if (wheelie && speed > 80f) 32f else 0f
        tilt += (target - tilt) * min(1f, dt * 5f)
    }
}
