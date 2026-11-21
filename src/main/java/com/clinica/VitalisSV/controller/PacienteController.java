package com.clinica.VitalisSV.controller;

import com.clinica.VitalisSV.dto.CitaResponse;
import com.clinica.VitalisSV.dto.PacienteResponse;
import com.clinica.VitalisSV.repository.PacienteRepository;
import com.clinica.VitalisSV.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final AgendaService agendaService;

    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream().map(PacienteResponse::from).toList();
    }

    @GetMapping("/{id}/citas")
    public List<CitaResponse> historial(@PathVariable Long id) {
        return agendaService.historialDePaciente(id).stream().map(CitaResponse::from).toList();
    }
}
