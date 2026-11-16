package com.clinica.VitalisSV.dto;

import com.clinica.VitalisSV.model.Medico;

public record MedicoResponse(Long id, String nombre, String apellido, String especialidad) {

    public static MedicoResponse from(Medico m) {
        return new MedicoResponse(m.getId(), m.getNombre(), m.getApellido(),
                m.getEspecialidad().getNombre());
    }
}
