import { useEffect, useState } from "react";
import { getTermini } from "../api/termini";
import { rezervisiTermin } from "../api/rezervacije";

function formatDatumVreme(iso) {
  const datum = new Date(iso);
  return datum.toLocaleString("sr-RS", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function TerminiPage() {
  const [termini, setTermini] = useState([]);
  const [ucitavanje, setUcitavanje] = useState(true);
  const [poruka, setPoruka] = useState(null);
  const [pretraga, setPretraga] = useState("");
  const [samoSlobodni, setSamoSlobodni] = useState(false);

  async function ucitajTermine() {
    setUcitavanje(true);
    try {
      const podaci = await getTermini();
      setTermini(podaci);
    } finally {
      setUcitavanje(false);
    }
  }

  useEffect(() => {
    ucitajTermine();
  }, []);

  async function handleRezervisi(termin) {
    setPoruka(null);
    try {
      const rezervacija = await rezervisiTermin(termin.id);
      if (rezervacija.status === "NA_CEKANJU") {
        setPoruka({ tip: "info", tekst: `Termin je popunjen. Dodati ste na listu čekanja za "${termin.trening.naziv}".` });
      } else {
        setPoruka({ tip: "success", tekst: `Uspešno rezervisan termin "${termin.trening.naziv}".` });
      }
      await ucitajTermine();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Rezervacija nije uspela" });
    }
  }

  if (ucitavanje) return <div className="container">Učitavanje termina...</div>;

  const pojamZaPretragu = pretraga.trim().toLowerCase();
  const filtriraniTermini = termini.filter((termin) => {
    if (samoSlobodni && termin.slobodnaMesta <= 0) return false;
    if (!pojamZaPretragu) return true;
    const tekst = `${termin.trening.naziv} ${termin.trener.ime} ${termin.trener.prezime}`.toLowerCase();
    return tekst.includes(pojamZaPretragu);
  });

  return (
    <div className="container">
      <h2>Slobodni termini</h2>
      {poruka && <div className={`alert-${poruka.tip}`}>{poruka.tekst}</div>}

      <div className="inline-form">
        <input
          placeholder="Pretraga po treningu ili treneru..."
          value={pretraga}
          onChange={(e) => setPretraga(e.target.value)}
        />
        <label style={{ display: "flex", alignItems: "center", gap: "0.4rem", fontSize: "0.9rem" }}>
          <input
            type="checkbox"
            checked={samoSlobodni}
            onChange={(e) => setSamoSlobodni(e.target.checked)}
          />
          Samo termini sa slobodnim mestima
        </label>
      </div>

      <div className="card-grid">
        {filtriraniTermini.length === 0 && <p>Nema termina koji odgovaraju pretrazi.</p>}
        {filtriraniTermini.map((termin) => (
          <div className="card" key={termin.id}>
            <h3>{termin.trening.naziv}</h3>
            <p className="muted">{termin.trening.opis}</p>
            <p>
              <strong>Kada:</strong> {formatDatumVreme(termin.datumVreme)}
            </p>
            <p>
              <strong>Trener:</strong> {termin.trener.ime} {termin.trener.prezime} ({termin.trener.specijalnost})
            </p>
            <p>
              <strong>Trajanje:</strong> {termin.trening.trajanjeMinuta} min
            </p>
            <p>
              <strong>Slobodna mesta:</strong>{" "}
              {termin.slobodnaMesta > 0 ? (
                <span className="badge-success">{termin.slobodnaMesta} / {termin.kapacitet}</span>
              ) : (
                <span className="badge-warning">Popunjeno ({termin.kapacitet}/{termin.kapacitet})</span>
              )}
            </p>
            <button onClick={() => handleRezervisi(termin)}>
              {termin.slobodnaMesta > 0 ? "Rezerviši termin" : "Prijavi se na listu čekanja"}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
