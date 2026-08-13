import { useEffect, useState } from "react";
import { getTreninzi, createTrening, updateTrening, deleteTrening } from "../api/treninzi";

const PRAZAN_FORM = { naziv: "", opis: "", trajanjeMinuta: 60 };

export function AdminTreninziPage() {
  const [treninzi, setTreninzi] = useState([]);
  const [form, setForm] = useState(PRAZAN_FORM);
  const [editId, setEditId] = useState(null);
  const [poruka, setPoruka] = useState(null);

  async function ucitaj() {
    setTreninzi(await getTreninzi());
  }

  useEffect(() => {
    ucitaj();
  }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm({ ...form, [name]: name === "trajanjeMinuta" ? Number(value) : value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setPoruka(null);
    try {
      if (editId) {
        await updateTrening(editId, form);
        setPoruka({ tip: "success", tekst: "Trening je izmenjen." });
      } else {
        await createTrening(form);
        setPoruka({ tip: "success", tekst: "Trening je dodat." });
      }
      setForm(PRAZAN_FORM);
      setEditId(null);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Greška prilikom čuvanja" });
    }
  }

  function handleEdit(trening) {
    setEditId(trening.id);
    setForm({ naziv: trening.naziv, opis: trening.opis || "", trajanjeMinuta: trening.trajanjeMinuta });
  }

  async function handleDelete(id) {
    setPoruka(null);
    try {
      await deleteTrening(id);
      await ucitaj();
    } catch (err) {
      setPoruka({ tip: "error", tekst: err.response?.data?.message || "Brisanje nije uspelo" });
    }
  }

  return (
    <div className="container">
      <h2>Upravljanje treninzima</h2>
      {poruka && <div className={`alert-${poruka.tip}`}>{poruka.tekst}</div>}

      <form className="inline-form" onSubmit={handleSubmit}>
        <input name="naziv" placeholder="Naziv" value={form.naziv} onChange={handleChange} required />
        <input name="opis" placeholder="Opis" value={form.opis} onChange={handleChange} />
        <input
          name="trajanjeMinuta"
          type="number"
          min="1"
          placeholder="Trajanje (min)"
          value={form.trajanjeMinuta}
          onChange={handleChange}
          required
        />
        <button type="submit">{editId ? "Sačuvaj izmene" : "Dodaj trening"}</button>
        {editId && (
          <button type="button" onClick={() => { setEditId(null); setForm(PRAZAN_FORM); }}>
            Otkaži
          </button>
        )}
      </form>

      <table className="table">
        <thead>
          <tr>
            <th>Naziv</th>
            <th>Opis</th>
            <th>Trajanje</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {treninzi.map((t) => (
            <tr key={t.id}>
              <td>{t.naziv}</td>
              <td className="muted">{t.opis}</td>
              <td>{t.trajanjeMinuta} min</td>
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
