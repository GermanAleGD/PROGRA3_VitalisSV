package com.clinica.VitalisSV.dto;

import com.clinica.VitalisSV.model.Paciente;

public record PacienteResponse(Long id, String nombre, String apellido, String documento) {

    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getDocumento());
    }
}
