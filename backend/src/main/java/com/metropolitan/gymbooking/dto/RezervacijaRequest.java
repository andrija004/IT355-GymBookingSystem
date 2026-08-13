package com.metropolitan.gymbooking.dto;

import jakarta.validation.constraints.NotNull;

public class RezervacijaRequest {

    @NotNull(message = "Termin je obavezan")
    private Long terminId;

    public Long getTerminId() {
        return terminId;
    }

    public void setTerminId(Long terminId) {
        this.terminId = terminId;
    }
}
