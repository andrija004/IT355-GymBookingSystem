import { useEffect, useState } from "react";
import { getMojeRezervacije, otkaziRezervaciju } from "../api/rezervacije";

const statusLabel = {
  POTVRDJENA: { text: "Potvrđena", cls: "badge-success" },
  NA_CEKANJU: { text: "Na čekanju", cls: "badge-warning" },
  OTKAZANA: { text: "Otkazana", cls: "badge-muted" },
};

function formatDatumVreme(iso) {
  return new Date(iso).toLocaleString("sr-RS", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function MojeRezervacijePage() {
  const [rezervacije, setRezervacije] = useState([]);
  const [ucitavanje, setUcitavanje] = useState(true);
  const [poruka, setPoruka] = useState(null);

  async function ucitaj() {
    setUcitavanje(true);
    try {
      const podaci = await getMojeRezervacije();
      setRezervacije(podaci);
    } finally {
      setUcitavanje(false);
    }
  }

  useEffect(() => {
    ucitaj();
  }, []);

  async function handleOtkazi(id) {
    setPoruka(null);
    try {
      await otkaziRezervaciju(id);
      setPoruka({ tip: "success", tekst: "Rezervacija je otkazana." });
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Otkazivanje nije uspelo" });
    }
  }

  if (ucitavanje) return <div className="container">Učitavanje...</div>;

  return (
    <div className="container">
      <h2>Moje rezervacije</h2>
      {poruka && <div className={`alert-${poruka.tip}`}>{poruka.tekst}</div>}
      {rezervacije.length === 0 && <p>Nemate rezervacija.</p>}
      <table className="table">
        <thead>
          <tr>
            <th>Trening</th>
            <th>Kada</th>
            <th>Trener</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {rezervacije.map((r) => (
            <tr key={r.id}>
              <td>{r.termin.trening.naziv}</td>
              <td>{formatDatumVreme(r.termin.datumVreme)}</td>
              <td>
                {r.termin.trener.ime} {r.termin.trener.prezime}
              </td>
              <td>
                <span className={statusLabel[r.status].cls}>{statusLabel[r.status].text}</span>
              </td>
              <td>
                {r.status !== "OTKAZANA" && (
                  <button className="btn-danger" onClick={() => handleOtkazi(r.id)}>
                    Otkaži
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
