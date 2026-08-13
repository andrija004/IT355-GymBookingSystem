import apiClient from "./client";

export function getTreninzi() {
  return apiClient.get("/treninzi").then((res) => res.data);
}

export function createTrening(trening) {
  return apiClient.post("/treninzi", trening).then((res) => res.data);
}

export function updateTrening(id, trening) {
  return apiClient.put(`/treninzi/${id}`, trening).then((res) => res.data);
}

export function deleteTrening(id) {
  return apiClient.delete(`/treninzi/${id}`);
}
