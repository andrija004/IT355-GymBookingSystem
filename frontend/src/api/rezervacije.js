import apiClient from "./client";

export function rezervisiTermin(terminId) {
  return apiClient.post("/rezervacije", { terminId }).then((res) => res.data);
}

export function otkaziRezervaciju(id) {
  return apiClient.delete(`/rezervacije/${id}`);
}

export function getMojeRezervacije() {
  return apiClient.get("/rezervacije/moje").then((res) => res.data);
}

export function getSveRezervacije() {
  return apiClient.get("/rezervacije").then((res) => res.data);
}
