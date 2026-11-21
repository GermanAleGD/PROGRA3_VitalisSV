package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.exception.ReglaNegocioException;
import com.clinica.VitalisSV.model.*;
import com.clinica.VitalisSV.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final HorarioDisponibleRepository horarioRepository;

    @Transactional
    public Cita agendar(Long pacienteId, Long medicoId,
                        LocalDateTime inicio, LocalDateTime fin, String motivo) {

        validarFechas(inicio, fin);

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ReglaNegocioException("El paciente no existe."));
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ReglaNegocioException("El medico no existe."));

        validarDisponibilidad(medicoId, inicio, fin, null);

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setInicio(inicio);
        cita.setFin(fin);
        cita.setMotivo(motivo);
        cita.setEstado(EstadoCita.PENDIENTE);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita reprogramar(Long citaId, LocalDateTime inicio, LocalDateTime fin) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ReglaNegocioException("La cita no existe."));

        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new ReglaNegocioException("Solo se pueden reprogramar citas pendientes o confirmadas.");
        }

        validarFechas(inicio, fin);
        validarDisponibilidad(cita.getMedico().getId(), inicio, fin, citaId);

        cita.setInicio(inicio);
        cita.setFin(fin);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cancelar(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ReglaNegocioException("La cita no existe."));
        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new ReglaNegocioException("No se puede cancelar una cita completada.");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        return citaRepository.save(cita);
    }

    @Transactional(readOnly = true)
    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    /** La cita debe ser coherente, de un solo dia y en el futuro. */
    private void validarFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La hora de fin debe ser posterior a la de inicio.");
        }
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new ReglaNegocioException("La cita debe empezar y terminar el mismo dia.");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se pueden agendar citas en el pasado.");
        }
    }

    /**
     * La franja debe caer dentro del horario del medico y no chocar con otra cita.
     * Si citaIdAExcluir no es null, esa cita se ignora (caso de reprogramar).
     */
    private void validarDisponibilidad(Long medicoId, LocalDateTime inicio, LocalDateTime fin,
                                       Long citaIdAExcluir) {
        Byte dia = (byte) inicio.getDayOfWeek().getValue(); // 1 = lunes ... 7 = domingo
        List<HorarioDisponible> horarios = horarioRepository.findByMedicoIdAndDiaSemana(medicoId, dia);

        boolean dentroDeHorario = horarios.stream().anyMatch(h ->
                !inicio.toLocalTime().isBefore(h.getHoraInicio())
                        && !fin.toLocalTime().isAfter(h.getHoraFin()));
        if (!dentroDeHorario) {
            throw new ReglaNegocioException("El medico no atiende en ese horario.");
        }

        boolean hayChoque = (citaIdAExcluir == null)
                ? citaRepository.existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                        medicoId, EstadoCita.CANCELADA, fin, inicio)
                : citaRepository.existsByMedicoIdAndIdNotAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                        medicoId, citaIdAExcluir, EstadoCita.CANCELADA, fin, inicio);
        if (hayChoque) {
            throw new ReglaNegocioException("El medico ya tiene una cita en ese horario.");
        }
    }
}
