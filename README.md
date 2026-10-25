# SportTimer

An interval timer for workouts: set the time per round, the number of rounds and an optional break.
It beeps every second in the last 10 seconds and when a round or break ends, and says "Well Done!" after the last round.

## Use it

Open https://sporttimer.kulose.de. On the iPhone, tap Share → "Add to Home Screen" once (while online). After that
the app opens and runs **without internet**: the timer runs on the device, the server only delivers the page.
The screen stays on while the timer runs. The settings are remembered on each device.

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
| `static/app.js` | the timer: rounds, breaks, beeps, keyboard, keeping the screen on |
| `static/sw.js` | keeps a copy of the app on the device, so it works offline |
| `static/manifest.webmanifest`, icons | name and icon for "Add to Home Screen" |
| `Dockerfile` | the image for the NAS |

The first version was a Swing desktop app; it is in the git history (before the web app commit).
