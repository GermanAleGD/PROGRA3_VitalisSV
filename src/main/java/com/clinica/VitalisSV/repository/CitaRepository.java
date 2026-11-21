package com.clinica.VitalisSV.repository;

import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Hay solapamiento si una cita existente del mismo medico (no cancelada)
     * empieza antes de que termine la nueva Y termina despues de que empiece la nueva.
     */
    boolean existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
            Long medicoId, EstadoCita estado, LocalDateTime fin, LocalDateTime inicio);

    /** Igual que la anterior, pero ignorando una cita concreta (para reprogramar). */
    boolean existsByMedicoIdAndIdNotAndEstadoNotAndInicioLessThanAndFinGreaterThan(
            Long medicoId, Long citaId, EstadoCita estado, LocalDateTime fin, LocalDateTime inicio);

    /** Agenda completa de un medico, sin las citas de un estado (normalmente CANCELADA). */
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByMedicoIdAndEstadoNotOrderByInicioAsc(Long medicoId, EstadoCita estado);

    /** Agenda de un medico en un rango [desde, hasta), por ejemplo un dia. */
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByMedicoIdAndEstadoNotAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
            Long medicoId, EstadoCita estado, LocalDateTime desde, LocalDateTime hasta);

    /** Historial de un paciente, de la mas reciente a la mas antigua. */
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByPacienteIdOrderByInicioDesc(Long pacienteId);

    @Override
    @EntityGraph(attributePaths = {"paciente", "medico"})
    Optional<Cita> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findAll();
}
