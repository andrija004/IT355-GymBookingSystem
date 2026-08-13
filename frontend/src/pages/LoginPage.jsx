import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [lozinka, setLozinka] = useState("");
  const [greska, setGreska] = useState("");
  const [ucitavanje, setUcitavanje] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setGreska("");
    setUcitavanje(true);
    try {
      await login(email, lozinka);
      navigate("/termini");
    } catch (err) {
      setGreska(err.response?.data?.message || "Prijava nije uspela");
    } finally {
      setUcitavanje(false);
    }
  }

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>Prijava</h2>
        {greska && <div className="alert-error">{greska}</div>}
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Lozinka
          <input type="password" value={lozinka} onChange={(e) => setLozinka(e.target.value)} required />
        </label>
        <button type="submit" disabled={ucitavanje}>
          {ucitavanje ? "Prijavljivanje..." : "Prijavi se"}
        </button>
        <p>
          Nemate nalog? <Link to="/registracija">Registrujte se</Link>
        </p>
        <p className="demo-hint">
          Demo admin: admin@teretana.rs / admin123
          <br />
          Demo korisnik: marko@primer.rs / marko123
        </p>
      </form>
    </div>
  );
}
