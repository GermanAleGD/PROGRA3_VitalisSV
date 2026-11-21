package com.clinica.VitalisSV.controller;

import com.clinica.VitalisSV.dto.AgendarCitaRequest;
import com.clinica.VitalisSV.dto.CitaResponse;
import com.clinica.VitalisSV.dto.ReprogramarCitaRequest;
import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse agendar(@Valid @RequestBody AgendarCitaRequest req) {
        Cita cita = citaService.agendar(
                req.pacienteId(), req.medicoId(), req.inicio(), req.fin(), req.motivo());
        return CitaResponse.from(cita);
    }

    @PatchMapping("/{id}/reprogramar")
    public CitaResponse reprogramar(@PathVariable Long id,
                                    @Valid @RequestBody ReprogramarCitaRequest req) {
        return CitaResponse.from(citaService.reprogramar(id, req.inicio(), req.fin()));
    }

    @PatchMapping("/{id}/cancelar")
    public CitaResponse cancelar(@PathVariable Long id) {
        return CitaResponse.from(citaService.cancelar(id));
    }

    @GetMapping
    public List<CitaResponse> listar() {
        return citaService.listar().stream().map(CitaResponse::from).toList();
    }
}
