package com.clinica.VitalisSV.dto;

import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long pacienteId,
        String paciente,
        Long medicoId,
        String medico,
        LocalDateTime inicio,
        LocalDateTime fin,
        EstadoCita estado,
        String motivo
) {
    public static CitaResponse from(Cita c) {
        return new CitaResponse(
                c.getId(),
                c.getPaciente().getId(),
                c.getPaciente().getNombre() + " " + c.getPaciente().getApellido(),
                c.getMedico().getId(),
                c.getMedico().getNombre() + " " + c.getMedico().getApellido(),
                c.getInicio(),
                c.getFin(),
                c.getEstado(),
                c.getMotivo());
    }
}
