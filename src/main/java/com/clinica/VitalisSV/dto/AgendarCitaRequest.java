package com.clinica.VitalisSV.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AgendarCitaRequest(
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fin,
        @Size(max = 255) String motivo
) {}
