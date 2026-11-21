package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.exception.RecursoNoEncontradoException;
import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;
import com.clinica.VitalisSV.repository.CitaRepository;
import com.clinica.VitalisSV.repository.MedicoRepository;
import com.clinica.VitalisSV.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    /** Citas activas (no canceladas) de un medico; si hay fecha, solo las de ese dia. */
    @Transactional(readOnly = true)
    public List<Cita> agendaDeMedico(Long medicoId, LocalDate fecha) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new RecursoNoEncontradoException("El medico no existe.");
        }
        if (fecha == null) {
            return citaRepository.findByMedicoIdAndEstadoNotOrderByInicioAsc(
                    medicoId, EstadoCita.CANCELADA);
        }
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = desde.plusDays(1);
        return citaRepository
                .findByMedicoIdAndEstadoNotAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
                        medicoId, EstadoCita.CANCELADA, desde, hasta);
    }

    /** Historial completo de un paciente, incluidas las citas canceladas. */
    @Transactional(readOnly = true)
    public List<Cita> historialDePaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new RecursoNoEncontradoException("El paciente no existe.");
        }
        return citaRepository.findByPacienteIdOrderByInicioDesc(pacienteId);
    }
}
