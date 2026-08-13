import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function Navbar() {
  const { korisnik, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/prijava");
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/termini">🏋 Teretana</Link>
      </div>
      <div className="navbar-links">
        {korisnik && (
          <>
            <Link to="/termini">Termini</Link>
            <Link to="/moje-rezervacije">Moje rezervacije</Link>
            {isAdmin && (
              <>
                <Link to="/admin/treneri">Treneri</Link>
                <Link to="/admin/treninzi">Treninzi</Link>
                <Link to="/admin/termini">Upravljanje terminima</Link>
              </>
            )}
          </>
        )}
      </div>
      <div className="navbar-user">
        {korisnik ? (
          <>
            <span>
              {korisnik.ime} {korisnik.prezime} ({korisnik.uloga})
            </span>
            <button onClick={handleLogout}>Odjava</button>
          </>
        ) : (
          <>
            <Link to="/prijava">Prijava</Link>
            <Link to="/registracija">Registracija</Link>
          </>
        )}
      </div>
    </nav>
  );
}
