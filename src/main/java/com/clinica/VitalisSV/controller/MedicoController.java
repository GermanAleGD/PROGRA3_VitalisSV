package com.clinica.VitalisSV.controller;

import com.clinica.VitalisSV.dto.CitaResponse;
import com.clinica.VitalisSV.dto.MedicoResponse;
import com.clinica.VitalisSV.repository.MedicoRepository;
import com.clinica.VitalisSV.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoRepository medicoRepository;
    private final AgendaService agendaService;

    @GetMapping
    public List<MedicoResponse> listar() {
        return medicoRepository.findAll().stream().map(MedicoResponse::from).toList();
    }

    @GetMapping("/{id}/citas")
    public List<CitaResponse> agenda(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return agendaService.agendaDeMedico(id, fecha).stream().map(CitaResponse::from).toList();
    }
}
