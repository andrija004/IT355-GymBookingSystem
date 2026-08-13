import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { ZasticenaRuta } from "./components/ZasticenaRuta";
import { Navbar } from "./components/Navbar";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { TerminiPage } from "./pages/TerminiPage";
import { MojeRezervacijePage } from "./pages/MojeRezervacijePage";
import { AdminTreneriPage } from "./pages/AdminTreneriPage";
import { AdminTreninziPage } from "./pages/AdminTreninziPage";
import { AdminTerminiPage } from "./pages/AdminTerminiPage";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/prijava" element={<LoginPage />} />
          <Route path="/registracija" element={<RegisterPage />} />
          <Route
            path="/termini"
            element={
              <ZasticenaRuta>
                <TerminiPage />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/moje-rezervacije"
            element={
              <ZasticenaRuta>
                <MojeRezervacijePage />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/treneri"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminTreneriPage />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/treninzi"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminTreninziPage />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/termini"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminTerminiPage />
              </ZasticenaRuta>
            }
          />
          <Route path="/" element={<Navigate to="/termini" replace />} />
          <Route path="*" element={<Navigate to="/termini" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
