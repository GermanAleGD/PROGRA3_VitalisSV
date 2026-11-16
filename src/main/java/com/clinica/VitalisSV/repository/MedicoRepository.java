package com.clinica.VitalisSV.repository;

import com.clinica.VitalisSV.model.Medico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Override
    @EntityGraph(attributePaths = {"especialidad"})
    List<Medico> findAll();
}
