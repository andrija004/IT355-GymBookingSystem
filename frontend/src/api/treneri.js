import apiClient from "./client";

export function getTreneri() {
  return apiClient.get("/treneri").then((res) => res.data);
}

export function createTrener(trener) {
  return apiClient.post("/treneri", trener).then((res) => res.data);
}

export function updateTrener(id, trener) {
  return apiClient.put(`/treneri/${id}`, trener).then((res) => res.data);
}

export function deleteTrener(id) {
  return apiClient.delete(`/treneri/${id}`);
}
