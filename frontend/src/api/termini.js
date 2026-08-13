import apiClient from "./client";

export function getTermini() {
  return apiClient.get("/termini").then((res) => res.data);
}

export function getSviTermini() {
  return apiClient.get("/termini/svi").then((res) => res.data);
}

export function createTermin(termin) {
  return apiClient.post("/termini", termin).then((res) => res.data);
}

export function updateTermin(id, termin) {
  return apiClient.put(`/termini/${id}`, termin).then((res) => res.data);
}

export function deleteTermin(id) {
  return apiClient.delete(`/termini/${id}`);
}
