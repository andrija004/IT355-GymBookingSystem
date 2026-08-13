import { createContext, useContext, useEffect, useState } from "react";
import * as authApi from "../api/auth";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [korisnik, setKorisnik] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem("korisnik");
    if (stored) {
      setKorisnik(JSON.parse(stored));
    }
    setLoading(false);
  }, []);

  function sacuvajSesiju(response) {
    const podaci = {
      email: response.email,
      ime: response.ime,
      prezime: response.prezime,
      uloga: response.uloga,
    };
    localStorage.setItem("token", response.token);
    localStorage.setItem("korisnik", JSON.stringify(podaci));
    setKorisnik(podaci);
  }

  async function login(email, lozinka) {
    const response = await authApi.login(email, lozinka);
    sacuvajSesiju(response);
  }

  async function register(ime, prezime, email, lozinka) {
    const response = await authApi.register(ime, prezime, email, lozinka);
    sacuvajSesiju(response);
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("korisnik");
    setKorisnik(null);
  }

  const value = {
    korisnik,
    loading,
    login,
    register,
    logout,
    isAdmin: korisnik?.uloga === "ADMIN",
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
