// Keeps a copy of the app on the device, so it opens and runs without internet.
// The copy is shown right away; when online, the newest files are fetched in the background
// and used the next time the app opens.

const CACHE = 'sporttimer';
const FILES = ['./', 'index.html', 'style.css', 'app.js', 'manifest.webmanifest',
    'favicon.png', 'apple-touch-icon.png', 'icon-192.png', 'icon-512.png',
    'voice-3.m4a', 'voice-2.m4a', 'voice-1.m4a'];

self.addEventListener('install', event => {
    event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(FILES)));
    self.skipWaiting();
});

self.addEventListener('activate', event => {
    event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', event => {
    if (event.request.method !== 'GET') {
        return;
    }
    event.respondWith(caches.open(CACHE).then(async cache => {
        const cached = await cache.match(event.request, {ignoreSearch: true});
        const fresh = fetch(event.request).then(response => {
            if (response.ok) {
                cache.put(event.request, response.clone());
            }
            return response;
        });
        if (cached) {
            event.waitUntil(fresh.catch(() => {})); // offline: the copy is enough
            return cached;
        }
        return fresh;
    }));
});
