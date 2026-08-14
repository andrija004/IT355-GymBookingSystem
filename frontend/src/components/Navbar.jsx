import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function Navbar() {
  const { korisnik, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/prijava");
  }

  const inicijali = korisnik ? `${korisnik.ime?.[0] || ""}${korisnik.prezime?.[0] || ""}`.toUpperCase() : "";

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <NavLink to="/termini">
          <span className="brand-badge">🏋</span>
          PULSE TERETANA
        </NavLink>
      </div>
      <div className="navbar-links">
        {korisnik && (
          <>
            <NavLink to="/termini">Termini</NavLink>
            <NavLink to="/moje-rezervacije">Moje rezervacije</NavLink>
            {isAdmin && (
              <>
                <NavLink to="/admin/treneri">Treneri</NavLink>
                <NavLink to="/admin/treninzi">Treninzi</NavLink>
                <NavLink to="/admin/termini">Upravljanje terminima</NavLink>
              </>
            )}
          </>
        )}
      </div>
      <div className="navbar-user">
        {korisnik ? (
          <>
            <div className="navbar-user-info">
              <span className="navbar-avatar">{inicijali}</span>
              <span>
                {korisnik.ime} {korisnik.prezime}
              </span>
              <span className="navbar-role">{isAdmin ? "Admin" : "Korisnik"}</span>
            </div>
            <button onClick={handleLogout}>Odjava</button>
          </>
        ) : (
          <>
            <NavLink to="/prijava">Prijava</NavLink>
            <NavLink to="/registracija">Registracija</NavLink>
          </>
        )}
      </div>
    </nav>
  );
}
