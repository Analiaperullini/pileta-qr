// auth.js — Spring Security gestiona la sesión en el servidor
function isLogged() { return true; }

function requireAuth() {
  // Spring Security redirige automáticamente a /login.html si no hay sesión activa
}

function loginOk() { }

function logout() {
  fetch('/logout', { method: 'POST' })
    .finally(() => { window.location.href = '/login.html'; });
}

// ─── Tema claro/oscuro ────────────────────────────────────────────────────────
function initTheme() {
  const t = localStorage.getItem('theme') || 'dark';
  document.documentElement.setAttribute('data-theme', t);
  _updateThemeBtn(t);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  localStorage.setItem('theme', next);
  document.documentElement.setAttribute('data-theme', next);
  _updateThemeBtn(next);
}

function _updateThemeBtn(theme) {
  const btn = document.getElementById('themeToggle');
  if (btn) btn.textContent = theme === 'dark' ? 'Modo claro' : 'Modo oscuro';
}