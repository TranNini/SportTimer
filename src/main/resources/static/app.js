// The timer runs here in the browser, so it keeps working without internet once the page is installed (sw.js).

const screen = document.getElementById('screen');
const roundLabel = document.getElementById('round');
const timerLabel = document.getElementById('time');
const startButton = document.getElementById('start');
const setTimeButton = document.getElementById('set-time');

const setupDialog = document.getElementById('setup');
const setupForm = document.getElementById('setup-form');
const timeField = document.getElementById('time-input');
const roundsField = document.getElementById('rounds-input');
const breakTimeField = document.getElementById('break-input');
const timePreview = document.getElementById('time-preview');
const breakPreview = document.getElementById('break-preview');
const setupError = document.getElementById('setup-error');
const modeHint = document.getElementById('mode-hint');

// How many seconds before the end of a round or break the countdown starts: beeps, then the voice says 3, 2, 1.
// Pilates needs time to get back into position.
const COUNTDOWN = {running: 3, pilates: 10};

// The settings (times in seconds) and the mode; remembered on this device
let settings = loadSettings();

let currentRound = 1;
let isBreak = false;
let isDone = false;
let started = false;     // false until Start, and again after Restart / Set Time
let running = false;
let endsAt = 0;          // while running: when the current round or break ends (ms)
let timeLeftMs = 0;      // while paused: what is left of it
let shownSeconds = 0;
let halfwaySaid = false;
let ticker = null;

// --- Settings ---

function loadSettings() {
    try {
        const saved = JSON.parse(localStorage.getItem('settings'));
        if (saved && saved.time > 0 && saved.rounds > 0 && saved.breakTime >= 0) {
            return {...saved, mode: COUNTDOWN[saved.mode] ? saved.mode : 'running'};
        }
    } catch (e) {
        // no saved settings (or no storage): use the defaults
    }
    return {time: 60, rounds: 1, breakTime: 0, mode: 'running'};
}

function saveSettings() {
    try {
        localStorage.setItem('settings', JSON.stringify(settings));
    } catch (e) {
        // not remembered, the timer works anyway
    }
}

// --- Timer ---

function resetTimer() {
    stopTicker();
    stopSpeaking();
    currentRound = 1;
    isBreak = false;
    isDone = false;
    started = false;
    halfwaySaid = false;
    timeLeftMs = settings.time * 1000;
    showTime(settings.time);
    updateRoundLabel();
    startButton.textContent = 'Start';
    setTimeButton.textContent = 'Set Time';
}

function start() {
    unlockSound();
    if (isDone) {
        resetTimer();
    }
    if (!started) {
        started = true;
        say(roundAnnouncement());
    }
    endsAt = Date.now() + timeLeftMs;
    running = true;
    ticker = setInterval(tick, 100);
    keepScreenOn();
    startButton.textContent = 'Pause';
    setTimeButton.textContent = 'Restart';
}

function pause() {
    timeLeftMs = Math.max(0, endsAt - Date.now());
    stopTicker();
    stopSpeaking();
    showTime(shownSeconds);
    startButton.textContent = 'Resume';
    setTimeButton.textContent = 'Restart';
}

function stopTicker() {
    clearInterval(ticker);
    ticker = null;
    running = false;
    letScreenSleep();
}

// Counts from the end time instead of adding up seconds, so it stays exact even if the
// browser slows the page down for a moment
function tick() {
    const now = Date.now();
    // More than one change is possible if the page slept (e.g. the phone was locked)
    while (running && now >= endsAt) {
        finishPhase(now - endsAt < 1000);
    }
    if (!running) {
        return;
    }
    const msLeft = endsAt - now;

    // "Halfway" in rounds of 20 seconds or more, not when catching up after a sleep
    const halfMs = settings.time * 500;
    if (!isBreak && !halfwaySaid && settings.time >= 20 && msLeft <= halfMs) {
        halfwaySaid = true;
        if (msLeft > halfMs - 1500) {
            say('Halfway');
        }
    }

    const seconds = Math.ceil(msLeft / 1000);
    if (seconds !== shownSeconds) {
        showTime(seconds);
        if (seconds <= 3) {
            say(String(seconds));
        } else if (seconds <= countdownSeconds()) {
            beep();
        }
    }
}

// The round or break that ended at endsAt is over: go on with the next one, or finish
function finishPhase(onTime) {
    if (isBreak) {
        isBreak = false;
        currentRound++;
        endsAt += settings.time * 1000;
    } else if (currentRound >= settings.rounds) {
        stopTicker();
        isDone = true;
        timeLeftMs = 0;
        showDone();
        startButton.textContent = 'Start';
        setTimeButton.textContent = 'Set Time';
        if (onTime) {
            beep(true);
            say('Well done!');
        }
        return;
    } else if (settings.breakTime > 0) {
        isBreak = true;
        endsAt += settings.breakTime * 1000;
    } else {
        currentRound++;
        endsAt += settings.time * 1000;
    }
    halfwaySaid = false;
    if (onTime) {
        beep(true);
        say(isBreak ? 'Break, ' + spokenDuration(settings.breakTime) : roundAnnouncement());
    }
    updateRoundLabel();
    showTime(Math.ceil((endsAt - Date.now()) / 1000));
}

function countdownSeconds() {
    return COUNTDOWN[settings.mode];
}

function showTime(seconds) {
    shownSeconds = seconds;
    timerLabel.textContent = formatTime(seconds);
    timerLabel.classList.remove('done');
    timerLabel.classList.toggle('ending', running && seconds <= countdownSeconds());
}

function showDone() {
    timerLabel.textContent = 'Well Done!';
    timerLabel.classList.remove('ending');
    timerLabel.classList.add('done');
}

function updateRoundLabel() {
    roundLabel.textContent = (isBreak ? 'Break ' : 'Round ') + currentRound + ' / ' + settings.rounds;
    screen.classList.toggle('break', isBreak);
}

// 90 -> "01:30", 45 -> "45"
function formatTime(totalSeconds) {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    if (minutes > 0) {
        return String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
    }
    return String(seconds);
}

// --- Sound ---

let audio = null;

// Browsers only play sound after a tap, so this runs on Start
function unlockSound() {
    if (navigator.audioSession) {
        // iPhone: mix with music or podcasts instead of stopping them (the silent switch then mutes the beeps)
        navigator.audioSession.type = 'ambient';
    }
    if (!audio) {
        const AudioContext = window.AudioContext || window.webkitAudioContext;
        audio = AudioContext ? new AudioContext() : null;
    }
    if (audio && audio.state !== 'running') {
        audio.resume();
    }
}

// A short beep for the countdown, a long one when a round or break ends
function beep(long) {
    if (!audio) {
        return;
    }
    const oscillator = audio.createOscillator();
    const volume = audio.createGain();
    const length = long ? 0.6 : 0.15;
    oscillator.frequency.value = long ? 1320 : 880;
    volume.gain.setValueAtTime(0.4, audio.currentTime);
    volume.gain.exponentialRampToValueAtTime(0.001, audio.currentTime + length);
    oscillator.connect(volume).connect(audio.destination);
    oscillator.start();
    oscillator.stop(audio.currentTime + length);
}

// --- Voice (the phone's own voices, so it works offline too) ---

function say(text) {
    if (!('speechSynthesis' in window)) {
        return;
    }
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'en-US';
    utterance.rate = 1.1;
    speechSynthesis.speak(utterance);
}

function stopSpeaking() {
    if ('speechSynthesis' in window) {
        speechSynthesis.cancel();
    }
}

function roundAnnouncement() {
    if (settings.rounds === 1) {
        return 'Go!';
    }
    if (currentRound === settings.rounds) {
        return 'Last round!';
    }
    return 'Round ' + currentRound + ' of ' + settings.rounds;
}

// 90 -> "1 minute 30", 45 -> "45 seconds", 120 -> "2 minutes"
function spokenDuration(totalSeconds) {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    if (minutes === 0) {
        return seconds + ' seconds';
    }
    const minuteText = minutes + (minutes === 1 ? ' minute' : ' minutes');
    return seconds === 0 ? minuteText : minuteText + ' ' + seconds;
}

// --- Screen stays on while the timer runs (a locked phone stops the page) ---

let wakeLock = null;

async function keepScreenOn() {
    if (!('wakeLock' in navigator) || wakeLock) {
        return;
    }
    try {
        wakeLock = await navigator.wakeLock.request('screen');
        wakeLock.addEventListener('release', () => wakeLock = null);
        if (!running) {
            letScreenSleep(); // paused while asking
        }
    } catch (e) {
        // not allowed right now (e.g. low battery mode); the timer runs anyway
    }
}

function letScreenSleep() {
    if (wakeLock) {
        wakeLock.release();
        wakeLock = null;
    }
}

// The browser drops the wake lock (and on the iPhone the sound) when the app goes to the background
document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible' && running) {
        keepScreenOn();
        if (audio && audio.state !== 'running') {
            audio.resume();
        }
    }
});

// --- Buttons and keys ---

startButton.addEventListener('click', () => running ? pause() : start());

setTimeButton.addEventListener('click', () => {
    if (setTimeButton.textContent === 'Set Time') {
        openSetup();
    } else {
        resetTimer();
    }
});

// Left / right pick a button, Enter / Space press it (Start if none is picked)
document.addEventListener('keydown', event => {
    if (setupDialog.open) {
        return;
    }
    if (event.key === 'ArrowLeft') {
        startButton.focus();
    } else if (event.key === 'ArrowRight') {
        setTimeButton.focus();
    } else if ((event.key === 'Enter' || event.key === ' ') && !(document.activeElement instanceof HTMLButtonElement)) {
        event.preventDefault();
        startButton.click();
    }
});

// --- Set Time ---

// Times are typed as mmss without the colon: 130 is 1:30, 45 is 0:45
function openSetup() {
    timeField.value = toMmss(settings.time);
    roundsField.value = settings.rounds;
    breakTimeField.value = settings.breakTime > 0 ? toMmss(settings.breakTime) : '';
    setupForm.elements.mode.value = settings.mode;
    setupError.textContent = '';
    updatePreviews();
    setupDialog.showModal();
    timeField.focus();
    timeField.select();
}

function toMmss(totalSeconds) {
    return String(Math.floor(totalSeconds / 60) * 100 + totalSeconds % 60);
}

function mmssToSeconds(input) {
    return Math.floor(input / 100) * 60 + input % 100;
}

function previewText(text) {
    const input = Number(text) || 0;
    return String(Math.floor(input / 100)).padStart(2, '0') + ':' + String(input % 100).padStart(2, '0');
}

function updatePreviews() {
    timePreview.textContent = 'Time ' + previewText(timeField.value);
    breakPreview.textContent = 'Break for ' + previewText(breakTimeField.value);
    modeHint.textContent = 'Countdown: last ' + COUNTDOWN[setupForm.elements.mode.value] + ' seconds';
}

for (const radio of setupForm.elements.mode) {
    radio.addEventListener('change', updatePreviews);
}

for (const field of [timeField, roundsField, breakTimeField]) {
    field.addEventListener('input', () => {
        field.value = field.value.replace(/\D/g, '').slice(0, 4);
        updatePreviews();
    });
    field.addEventListener('focus', () => field.select());
}

// Enter goes to the next field, and confirms in the last one; Space confirms right away
timeField.addEventListener('keydown', event => nextFieldOnEnter(event, roundsField));
roundsField.addEventListener('keydown', event => nextFieldOnEnter(event, breakTimeField));

function nextFieldOnEnter(event, next) {
    if (event.key === 'Enter') {
        event.preventDefault();
        next.focus();
    }
}

setupDialog.addEventListener('keydown', event => {
    if (event.key === ' ' && event.target instanceof HTMLInputElement && event.target.type !== 'radio') {
        event.preventDefault();
        setupForm.requestSubmit();
    }
});

document.getElementById('cancel').addEventListener('click', () => setupDialog.close());

setupForm.addEventListener('submit', event => {
    event.preventDefault();
    const timeInput = Number(timeField.value);
    const rounds = Number(roundsField.value);
    const breakInput = Number(breakTimeField.value || 0);

    if (!timeField.value || timeInput % 100 >= 60 || mmssToSeconds(timeInput) <= 0) {
        return showSetupError('Time must be bigger than 0, with at most 59 seconds', timeField);
    }
    if (!roundsField.value || rounds <= 0) {
        return showSetupError('Rounds must be bigger than 0', roundsField);
    }
    if (breakInput % 100 >= 60) {
        return showSetupError('The break can have at most 59 seconds', breakTimeField);
    }

    settings = {time: mmssToSeconds(timeInput), rounds: rounds, breakTime: mmssToSeconds(breakInput),
        mode: setupForm.elements.mode.value};
    saveSettings();
    resetTimer();
    setupDialog.close();
});

function showSetupError(message, field) {
    setupError.textContent = message + ' Ò.ó';
    field.focus();
    field.select();
}

resetTimer();

// --- Offline ---

if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('sw.js');
}
