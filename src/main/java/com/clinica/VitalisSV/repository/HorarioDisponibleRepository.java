package com.clinica.VitalisSV.repository;

import com.clinica.VitalisSV.model.HorarioDisponible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioDisponibleRepository extends JpaRepository<HorarioDisponible, Long> {

    List<HorarioDisponible> findByMedicoIdAndDiaSemana(Long medicoId, Byte diaSemana);
}
