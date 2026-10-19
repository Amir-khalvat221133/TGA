# MotoStreet (prototype)
Side-scrolling 2D Android prototype: a cartoon redraw of a yellow Honda CG125-style bike riding an alley street.

## Controls (bottom-right buttons, right to left)
Gas · Wheelie (tilts bike, needs speed) · Brake. Multi-touch supported. Camera follows with look-ahead.

## Structure
- `BikeState.kt` – physics/state · `BikeRenderer.kt` – vector bike art (edit shapes to refine)
- `StreetRenderer.kt` – parallax buildings, road, cars · `GameView.kt` – loop, input, camera, HUD

## Build
Push to GitHub → Actions → "Android APK" → download artifact `MotoStreet-debug-apk`.
Local: `gradle assembleDebug` (Gradle 8.7, JDK 17, Android SDK 34).

## Assumptions
- Bike details come from one photo (left-side view); right side, rear and exact decals are not visible and not modelled.
- Rider/cars/buildings are original art inspired by the reference game's style, not copies.
- Cars are scenery only (no collision). Progress is not saved. No audio.
