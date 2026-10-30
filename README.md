# SportTimer

An interval timer for workouts: set the time per round, the number of rounds and an optional break.
For running, pilates and other workouts. Pick the mode in Set Time:

| Mode | Countdown before the end of a round or break |
|---|---|
| Running | the last 3 seconds |
| Pilates | the last 10 seconds, time to get back into position |

During the countdown the time turns the phase's color: green in a round, orange in a break. "Well Done!" is green.

Sounds:
- a voice says "Round 2 of 5" (or "Last round!"), "Break, 30 seconds", "Halfway" (rounds of 20 s or more),
  counts "3, 2, 1" and says "Well done!" at the end
- short beeps during the countdown before the "3, 2, 1" (Pilates), a long beep when a round or break ends
- the sounds mix with music or podcasts instead of stopping them; the silent switch mutes the beeps

## Use it

Open https://sporttimer.kulose.de. On the iPhone, tap Share → "Add to Home Screen" once (while online). After that
the app opens and runs **without internet**: the timer runs on the device, the server only delivers the page.
The screen stays on while the timer runs; don't lock the phone, a locked phone pauses the page (no sounds),
and it catches up when unlocked. The settings are remembered on each device.

- **Set Time**: Running or Pilates, then times typed without the colon, e.g. `130` = 1:30, `45` = 0:45. Leave the break empty for none.
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
| `static/app.js` | the timer: rounds, breaks, modes, beeps, voice, keyboard, keeping the screen on |
| `static/voice-3.m4a`, `-2`, `-1` | the recorded "3, 2, 1", so the countdown is on time |
| `static/sw.js` | keeps a copy of the app on the device, so it works offline |
| `static/manifest.webmanifest`, icons | name and icon for "Add to Home Screen" |
| `Dockerfile` | the image for the NAS |

The first version was a Swing desktop app; it is in the git history (before the web app commit).
