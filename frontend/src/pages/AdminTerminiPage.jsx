import { useEffect, useState } from "react";
import { getSviTermini, createTermin, updateTermin, deleteTermin } from "../api/termini";
import { getTreneri } from "../api/treneri";
import { getTreninzi } from "../api/treninzi";

const PRAZAN_FORM = { datumVreme: "", kapacitet: 10, treningId: "", trenerId: "" };

function formatDatumVreme(iso) {
  return new Date(iso).toLocaleString("sr-RS", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function AdminTerminiPage() {
  const [termini, setTermini] = useState([]);
  const [treneri, setTreneri] = useState([]);
  const [treninzi, setTreninzi] = useState([]);
  const [form, setForm] = useState(PRAZAN_FORM);
  const [editId, setEditId] = useState(null);
  const [poruka, setPoruka] = useState(null);

  async function ucitaj() {
    const [t, tr, tn] = await Promise.all([getSviTermini(), getTreneri(), getTreninzi()]);
    setTermini(t);
    setTreneri(tr);
    setTreninzi(tn);
  }

  useEffect(() => {
    ucitaj();
  }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm({ ...form, [name]: name === "kapacitet" ? Number(value) : value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setPoruka(null);
    const payload = {
      datumVreme: form.datumVreme,
      kapacitet: form.kapacitet,
      treningId: Number(form.treningId),
      trenerId: Number(form.trenerId),
    };
    try {
      if (editId) {
        await updateTermin(editId, payload);
        setPoruka({ tip: "success", tekst: "Termin je izmenjen." });
      } else {
        await createTermin(payload);
        setPoruka({ tip: "success", tekst: "Termin je zakazan." });
      }
      setForm(PRAZAN_FORM);
      setEditId(null);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Greška prilikom čuvanja" });
    }
  }

  function handleEdit(termin) {
    setEditId(termin.id);
    setForm({
      datumVreme: termin.datumVreme.slice(0, 16),
      kapacitet: termin.kapacitet,
      treningId: termin.trening.id,
      trenerId: termin.trener.id,
    });
  }

  async function handleDelete(id) {
    setPoruka(null);
    try {
      await deleteTermin(id);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Brisanje nije uspelo" });
    }
  }

  return (
    <div className="container">
      <h2>Upravljanje terminima</h2>
      {poruka && <div className={`alert-${poruka.tip}`}>{poruka.tekst}</div>}

      <form className="inline-form" onSubmit={handleSubmit}>
        <input type="datetime-local" name="datumVreme" value={form.datumVreme} onChange={handleChange} required />
        <select name="treningId" value={form.treningId} onChange={handleChange} required>
          <option value="">Trening...</option>
          {treninzi.map((t) => (
            <option key={t.id} value={t.id}>{t.naziv}</option>
          ))}
        </select>
        <select name="trenerId" value={form.trenerId} onChange={handleChange} required>
          <option value="">Trener...</option>
          {treneri.map((t) => (
            <option key={t.id} value={t.id}>{t.ime} {t.prezime}</option>
          ))}
        </select>
        <input
          name="kapacitet"
          type="number"
          min="1"
          placeholder="Kapacitet"
          value={form.kapacitet}
          onChange={handleChange}
          required
        />
        <button type="submit">{editId ? "Sačuvaj izmene" : "Zakaži termin"}</button>
        {editId && (
          <button type="button" onClick={() => { setEditId(null); setForm(PRAZAN_FORM); }}>
            Otkaži
          </button>
        )}
      </form>

      <table className="table">
        <thead>
          <tr>
            <th>Trening</th>
            <th>Kada</th>
            <th>Trener</th>
            <th>Popunjenost</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {termini.map((t) => (
            <tr key={t.id}>
              <td>{t.trening.naziv}</td>
              <td>{formatDatumVreme(t.datumVreme)}</td>
              <td>{t.trener.ime} {t.trener.prezime}</td>
              <td>{t.brojPotvrdjenih} / {t.kapacitet}</td>
              <td>
                <button onClick={() => handleEdit(t)}>Izmeni</button>
                <button className="btn-danger" onClick={() => handleDelete(t.id)}>Obriši</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
