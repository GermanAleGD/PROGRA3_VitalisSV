package com.clinica.VitalisSV.dto;

import com.clinica.VitalisSV.model.Paciente;

import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        String nombre,
        String apellido,
        String documento,
        String correo,
        String telefono,
        LocalDate fechaNacimiento
) {
    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getDocumento(),
                p.getCorreo(), p.getTelefono(), p.getFechaNacimiento());
    }
}
