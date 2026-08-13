import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function ZasticenaRuta({ children, samoAdmin = false }) {
  const { korisnik, loading, isAdmin } = useAuth();

  if (loading) {
    return null;
  }

  if (!korisnik) {
    return <Navigate to="/prijava" replace />;
  }

  if (samoAdmin && !isAdmin) {
    return <Navigate to="/termini" replace />;
  }

  return children;
}
