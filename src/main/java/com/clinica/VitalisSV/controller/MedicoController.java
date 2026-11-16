package com.clinica.VitalisSV.controller;

import com.clinica.VitalisSV.dto.MedicoResponse;
import com.clinica.VitalisSV.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoRepository medicoRepository;

    @GetMapping
    public List<MedicoResponse> listar() {
        return medicoRepository.findAll().stream().map(MedicoResponse::from).toList();
    }
}
