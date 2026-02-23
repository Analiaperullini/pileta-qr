// auth.js
const AUTH_KEY = "pileta_auth_ok";

function isLogged() {
  return sessionStorage.getItem(AUTH_KEY) === "1";
}

function requireAuth() {
  if (!isLogged()) window.location.href = "/login.html";
}

function loginOk() {
  sessionStorage.setItem(AUTH_KEY, "1");
}

function logout() {
  sessionStorage.removeItem(AUTH_KEY);
  window.location.href = "/login.html";
}