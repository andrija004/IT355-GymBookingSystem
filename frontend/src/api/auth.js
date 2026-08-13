import apiClient from "./client";

export function login(email, lozinka) {
  return apiClient.post("/auth/login", { email, lozinka }).then((res) => res.data);
}

export function register(ime, prezime, email, lozinka) {
  return apiClient
    .post("/auth/register", { ime, prezime, email, lozinka })
    .then((res) => res.data);
}
