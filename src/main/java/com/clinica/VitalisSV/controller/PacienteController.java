package com.clinica.VitalisSV.controller;

import com.clinica.VitalisSV.dto.CitaResponse;
import com.clinica.VitalisSV.dto.PacienteRequest;
import com.clinica.VitalisSV.dto.PacienteResponse;
import com.clinica.VitalisSV.repository.PacienteRepository;
import com.clinica.VitalisSV.service.AgendaService;
import com.clinica.VitalisSV.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final PacienteService pacienteService;
    private final AgendaService agendaService;

    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream().map(PacienteResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PacienteResponse obtener(@PathVariable Long id) {
        return PacienteResponse.from(pacienteService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponse crear(@Valid @RequestBody PacienteRequest req) {
        return PacienteResponse.from(pacienteService.crear(req));
    }

    @PutMapping("/{id}")
    public PacienteResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody PacienteRequest req) {
        return PacienteResponse.from(pacienteService.actualizar(id, req));
    }

    @GetMapping("/{id}/citas")
    public List<CitaResponse> historial(@PathVariable Long id) {
        return agendaService.historialDePaciente(id).stream().map(CitaResponse::from).toList();
    }
}
