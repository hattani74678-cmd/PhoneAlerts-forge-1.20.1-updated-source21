# Phone Alerts (Forge 1.20.1)

Arabic/English in-game alert system for Minecraft Forge 1.20.1 (Forge 47.3.0).

## Features
- Glass-style alert banner anchored near the left edge of the screen.
- iPhone-like alert tone.
- Admin panel opened with **K** (rebindable in Controls -> Phone Alerts).
- Alert types: hostile threat, restricted zone, all-clear, heavy rain, thunderstorm, evacuation, projectile hazard, and custom message.
- Admin-only send permission and optional weather control.

## Build
Requires JDK 17 and Gradle 8.8. Run `gradle build`. The output JAR is in `build/libs/`.
A GitHub Actions workflow is included at `.github/workflows/build.yml`; push the project to GitHub and run the **Build Forge mod** workflow to download the JAR artifact.

## Commands
`/phonealert <hostile|zone|clear|rain|storm|evac|projectiles|custom> [radius] [message]`

Commands require the configured permission level (default: OP level 2).
