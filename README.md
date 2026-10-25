# SportTimer

An interval timer for workouts: set the time per round, the number of rounds and an optional break.
For running, pilates and other workouts: the screen turns orange during a round, teal during a break and green at the end,
with a ring that empties as the time runs out and a line showing what comes next.

Sounds:
- a voice says "Round 2 of 5" (or "Last round!"), "Break, 30 seconds", "Halfway" (rounds of 20 s or more),
  counts "3, 2, 1" and says "Well done!" at the end
- a short beep from 5 to 4 seconds before the end of a round, from 10 to 4 before the end of a break (time to get
  back into position), and a long one when a round or break ends
- the sounds mix with music or podcasts instead of stopping them; the silent switch mutes the beeps

## Use it

Open https://sporttimer.kulose.de. On the iPhone, tap Share → "Add to Home Screen" once (while online). After that
the app opens and runs **without internet**: the timer runs on the device, the server only delivers the page.
The screen stays on while the timer runs; don't lock the phone, a locked phone pauses the page (no sounds),
and it catches up when unlocked. The settings are remembered on each device.

- **Set Time**: times are typed without the colon, e.g. `130` = 1:30, `45` = 0:45. Leave the break empty for none.
- **Start / Pause / Resume**, and **Restart** to go back to round 1.
- Keyboard: ← / → pick a button, Enter or Space presses it. In Set Time, Enter goes to the next field, Space confirms, Esc cancels.

## Run locally

    mvn spring-boot:run

or run `sporttimer.SportTimerApp` in IntelliJ, then open http://localhost:8082.
(Offline use needs HTTPS, so it only works on localhost or https://sporttimer.kulose.de.)

## Project structure

| File | What it does |
|---|---|
| `SportTimerApp.java` | starts the app, which only serves the files in `static/` |
| `static/index.html`, `style.css` | the timer screen and the Set Time dialog |
| `static/app.js` | the timer: rounds, breaks, beeps, voice, colors and ring, keyboard, keeping the screen on |
| `static/sw.js` | keeps a copy of the app on the device, so it works offline |
| `static/manifest.webmanifest`, icons | name and icon for "Add to Home Screen" |
| `Dockerfile` | the image for the NAS |

The first version was a Swing desktop app; it is in the git history (before the web app commit).
