package com.clinica.VitalisSV.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReprogramarCitaRequest(
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fin
) {}
