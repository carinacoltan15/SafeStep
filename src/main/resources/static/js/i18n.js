/* ══════════════════════════════════════════════════════════════
   SafeStep — i18n  (EN / RO)
   Usage:
     - Add data-i18n="key" to text elements
     - Add data-i18n-ph="key" to inputs (sets placeholder)
     - Language is stored in localStorage under 'appLanguage'
     - Call ssSetLang('EN') or ssSetLang('RO') to switch
══════════════════════════════════════════════════════════════ */

const SS_LANG = {
    EN: {
        // ── index.html ──────────────────────────────────────────
        'portal.tagline':         'MISSION \u00B7 SAFETY \u00B7 ADVENTURE',
        'portal.compass.sub':     'Navigate Safe',
        'portal.parent.badge':    'SECURE PORTAL',
        'portal.parent.heading':  'Parent Gateway',
        'portal.parent.btn':      'Access Dashboard',
        'portal.child.badge':     'LIVE MISSION',
        'portal.child.heading':   'Child Quest',
        'portal.child.btn':       'Select Avatar',
        'portal.footer':          'SAFE STEP \u00A0\u2022\u00A0 PREMIUM ECOSYSTEM',
        // ── parent_login.html ────────────────────────────────────
        'login.title':            'Parent Portal',
        'login.subtitle':         "Securely track your child\u2019s journey",
        'login.email.ph':         'parent@safestep.com',
        'login.pass.ph':          'Password',
        'login.pass.show':        'Show',
        'login.pass.hide':        'Hide',
        'login.btn':              'SIGN IN',
        'login.no-account':       'New here?',
        'login.register':         'CREATE ACCOUNT',
        'login.back':             'Back to Start',
        // ── child_map.html ───────────────────────────────────────
        'mission.ready.title':    'Get Ready for Mission',
        'mission.ready.sub':      'Choose your hero, enter your name and the pairing code from your parent. Once connected, the simulation starts and mission cards are unlocked.',
        'mission.start.btn':      'START MISSION',
        'mission.demo.btn':       'Demo Autofill',
        'mission.complete.title': 'MISSION COMPLETE',
        'mission.wait.btn':       'Wait for New Mission',
        'mission.here.btn':       'I AM HERE',
        'mission.deviate.btn':    'Simulate Deviation',
        'mission.sim.label':      'Simulator Mode',
        'mission.gps.label':      'Real GPS Mode',
        'mission.switch.btn':     'Switch Account / Reset',
    },
    RO: {
        // ── index.html ──────────────────────────────────────────
        'portal.tagline':         'MISIUNE \u00B7 SIGURAN\u021A\u0102 \u00B7 AVENTUR\u0102',
        'portal.compass.sub':     'Navighează în Siguranță',
        'portal.parent.badge':    'PORTAL SECURIZAT',
        'portal.parent.heading':  'Zona Părintelui',
        'portal.parent.btn':      'Accesează Dashboard',
        'portal.child.badge':     'MISIUNE LIVE',
        'portal.child.heading':   'Zona Copilului',
        'portal.child.btn':       'Alege Avatar',
        'portal.footer':          'SAFE STEP \u00A0\u2022\u00A0 ECOSISTEM PREMIUM',
        'avatar.title':           'Cine ești tu, Erou?',
        'avatar.sub':             'ALEGE-\u021AI IDENTITATEA \u00B7 MISIUNEA \u00CENCEPE',
        'avatar.back':            '\u2715\u00A0\u00A0\u00CENAPO\u00CE',
        // ── parent_login.html ────────────────────────────────────
        'login.title':            'Portal Părinte',
        'login.subtitle':         'Monitorizează traseul copilului în siguranță',
        'login.email.ph':         'parent@safestep.com',
        'login.pass.ph':          'Parolă',
        'login.pass.show':        'Arată',
        'login.pass.hide':        'Ascunde',
        'login.btn':              'AUTENTIFICARE',
        'login.no-account':       'Nu ai cont?',
        'login.register':         'CREEAZĂ CONT',
        'login.back':             'Înapoi la Start',
        // ── child_map.html ───────────────────────────────────────
        'mission.ready.title':    'Pregătit pentru Misiune',
        'mission.ready.sub':      'Alege-ți eroul, introdu numele și codul de la părinte. Odată conectat, simularea pornește și cardurile de misiune se deblochează.',
        'mission.start.btn':      'PORNIRE MISIUNE',
        'mission.demo.btn':       'Demo Autofill',
        'mission.complete.title': 'MISIUNE COMPLETATĂ',
        'mission.wait.btn':       'Așteaptă Misiune Nouă',
        'mission.here.btn':       'SUNT AICI',
        'mission.deviate.btn':    'Simulează Deviere',
        'mission.sim.label':      'Mod Simulator',
        'mission.gps.label':      'GPS Real',
        'mission.switch.btn':     'Schimbă Cont / Reset',
    }
};

function ssGetLang() {
    return localStorage.getItem('appLanguage') || 'EN';
}

function ssSetLang(lang) {
    localStorage.setItem('appLanguage', lang);
    ssApplyLang(lang);
    document.querySelectorAll('.lang-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.lang === lang);
    });
}

function ssT(key) {
    const lang = ssGetLang();
    return (SS_LANG[lang] && SS_LANG[lang][key] !== undefined)
        ? SS_LANG[lang][key]
        : (SS_LANG['EN'][key] || key);
}

function ssApplyLang(lang) {
    const dict = SS_LANG[lang] || SS_LANG['EN'];
    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.dataset.i18n;
        if (dict[key] !== undefined) el.textContent = dict[key];
    });
    document.querySelectorAll('[data-i18n-ph]').forEach(el => {
        const key = el.dataset.i18nPh;
        if (dict[key] !== undefined) el.placeholder = dict[key];
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const lang = ssGetLang();
    ssApplyLang(lang);
    document.querySelectorAll('.lang-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.lang === lang);
    });
});
