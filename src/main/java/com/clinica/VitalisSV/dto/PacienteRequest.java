package com.clinica.VitalisSV.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotBlank @Size(max = 30) String documento,
        @Email @Size(max = 150) String correo,
        @Size(max = 30) String telefono,
        @Past LocalDate fechaNacimiento
) {}
