package com.clinica.VitalisSV.repository;

import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Hay solapamiento si una cita existente del mismo medico (no cancelada)
     * empieza antes de que termine la nueva Y termina despues de que empiece la nueva.
     * Parametros: medico, estado a excluir, fin de la nueva, inicio de la nueva.
     */
    boolean existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
            Long medicoId, EstadoCita estado, LocalDateTime fin, LocalDateTime inicio);
}
