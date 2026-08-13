import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function RegisterPage() {
  const [ime, setIme] = useState("");
  const [prezime, setPrezime] = useState("");
  const [email, setEmail] = useState("");
  const [lozinka, setLozinka] = useState("");
  const [greska, setGreska] = useState("");
  const [ucitavanje, setUcitavanje] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setGreska("");
    setUcitavanje(true);
    try {
      await register(ime, prezime, email, lozinka);
      navigate("/termini");
    } catch (err) {
      const data = err.response?.data;
      setGreska(data?.message || (data?.errors && Object.values(data.errors).join(", ")) || "Registracija nije uspela");
    } finally {
      setUcitavanje(false);
    }
  }

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>Registracija</h2>
        {greska && <div className="alert-error">{greska}</div>}
        <label>
          Ime
          <input value={ime} onChange={(e) => setIme(e.target.value)} required />
        </label>
        <label>
          Prezime
          <input value={prezime} onChange={(e) => setPrezime(e.target.value)} required />
        </label>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Lozinka
          <input
            type="password"
            value={lozinka}
            onChange={(e) => setLozinka(e.target.value)}
            minLength={6}
            required
          />
        </label>
        <button type="submit" disabled={ucitavanje}>
          {ucitavanje ? "Kreiranje naloga..." : "Registruj se"}
        </button>
        <p>
          Već imate nalog? <Link to="/prijava">Prijavite se</Link>
        </p>
      </form>
    </div>
  );
}
