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

    @Override
    @EntityGraph(attributePaths = {"paciente", "medico"})
    Optional<Cita> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findAll();
}
