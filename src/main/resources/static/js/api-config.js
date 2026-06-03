/**
 * SafeStep API resolver
 *
 * Works in all three environments:
 *  1. Local Spring Boot (http://192.168.x.x:8080 or http://localhost:8080)
 *  2. Production HTTPS on Render (https://your-app.onrender.com)
 *  3. Local static file (file://) — dev without a server
 */
(function (global) {
    'use strict';

    function resolveApiBase() {
        const loc   = global.location;
        const port  = loc.port;
        const proto = loc.protocol;

        // ── file:// (open HTML directly without a server) ─────────────────
        if (proto === 'file:') {
            const saved = localStorage.getItem('safestep_api_base');
            if (saved) return saved.replace(/\/$/, '');
            return 'http://127.0.0.1:8080/api';
        }

        // ── Served from Spring Boot directly ──────────────────────────────
        // Matches: localhost:8080, 192.168.x.x:8080,
        //          https://xxx.onrender.com (no explicit port → HTTPS port 443)
        if (port === '8080' || port === '80' || proto === 'https:' || !port) {
            return '/api';
        }

        // ── Other local dev server (e.g. VS Code Live Server :5500) ───────
        // Hit the backend on the same host but explicit port 8080
        return proto + '//' + loc.hostname + ':8080/api';
    }

    global.SAFESTEP_API = resolveApiBase();

    global.apiUrl = function (path) {
        const p = path.startsWith('/') ? path : '/' + path;
        return global.SAFESTEP_API + p;
    };
})(window);
