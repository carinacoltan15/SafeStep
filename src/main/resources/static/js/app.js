// Adresa de bază a Backend-ului tău
const API_URL = "http://localhost:8080/api";

// 1. Funcție pentru calcularea distanței (Formula Haversine)
// O poți folosi pentru SafeBot să-i spui copilului câți metri mai are
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371e3; // Raza Pământului în metri
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return (R * c).toFixed(0); // Returnează distanța în metri
}

// 2. Gestionarea Avatarului
function getAvatar() {
    return localStorage.getItem('avatar') || '🚀';
}

// 3. Notificări Premium (SweetAlert2)
// O funcție globală pentru a afișa mesaje frumoase
function showMessage(title, text, icon = 'info') {
    Swal.fire({
        title: title,
        text: text,
        icon: icon,
        confirmButtonText: 'Am înțeles',
        confirmButtonColor: '#ff823a'
    });
}

// 4. Resetare completă sesiune (Logout)
function resetApp() {
    localStorage.clear();
    window.location.href = 'index.html';
}

// 5. Verificare Backend (Opțional, pentru debug)
console.log("SafeStep Logic Loaded. API connected to: " + API_URL);