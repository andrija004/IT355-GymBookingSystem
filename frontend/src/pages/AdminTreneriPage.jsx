import { useEffect, useState } from "react";
import { getTreneri, createTrener, updateTrener, deleteTrener } from "../api/treneri";

const PRAZAN_FORM = { ime: "", prezime: "", specijalnost: "", biografija: "" };

export function AdminTreneriPage() {
  const [treneri, setTreneri] = useState([]);
  const [form, setForm] = useState(PRAZAN_FORM);
  const [editId, setEditId] = useState(null);
  const [poruka, setPoruka] = useState(null);

  async function ucitaj() {
    setTreneri(await getTreneri());
  }

  useEffect(() => {
    ucitaj();
  }, []);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setPoruka(null);
    try {
      if (editId) {
        await updateTrener(editId, form);
        setPoruka({ tip: "success", tekst: "Trener je izmenjen." });
      } else {
        await createTrener(form);
        setPoruka({ tip: "success", tekst: "Trener je dodat." });
      }
      setForm(PRAZAN_FORM);
      setEditId(null);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Greška prilikom čuvanja" });
    }
  }

  function handleEdit(trener) {
    setEditId(trener.id);
    setForm({ ime: trener.ime, prezime: trener.prezime, specijalnost: trener.specijalnost, biografija: trener.biografija || "" });
  }

  async function handleDelete(id) {
    setPoruka(null);
    try {
      await deleteTrener(id);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Brisanje nije uspelo" });
    }
  }

  return (
    <div className="container">
      <h2>Upravljanje trenerima</h2>
      {poruka && <div className={`alert-${poruka.tip}`}>{poruka.tekst}</div>}

      <form className="inline-form" onSubmit={handleSubmit}>
        <input name="ime" placeholder="Ime" value={form.ime} onChange={handleChange} required />
        <input name="prezime" placeholder="Prezime" value={form.prezime} onChange={handleChange} required />
        <input name="specijalnost" placeholder="Specijalnost" value={form.specijalnost} onChange={handleChange} required />
        <input name="biografija" placeholder="Biografija" value={form.biografija} onChange={handleChange} />
        <button type="submit">{editId ? "Sačuvaj izmene" : "Dodaj trenera"}</button>
        {editId && (
          <button type="button" onClick={() => { setEditId(null); setForm(PRAZAN_FORM); }}>
            Otkaži
          </button>
        )}
      </form>

      <table className="table">
        <thead>
          <tr>
            <th>Ime i prezime</th>
            <th>Specijalnost</th>
            <th>Biografija</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {treneri.map((t) => (
            <tr key={t.id}>
              <td>{t.ime} {t.prezime}</td>
              <td>{t.specijalnost}</td>
              <td className="muted">{t.biografija}</td>
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
