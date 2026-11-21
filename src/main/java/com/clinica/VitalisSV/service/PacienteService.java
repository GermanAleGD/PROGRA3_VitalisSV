package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.dto.PacienteRequest;
import com.clinica.VitalisSV.exception.RecursoNoEncontradoException;
import com.clinica.VitalisSV.exception.ReglaNegocioException;
import com.clinica.VitalisSV.model.Paciente;
import com.clinica.VitalisSV.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional
    public Paciente crear(PacienteRequest req) {
        String documento = req.documento().trim();
        if (pacienteRepository.existsByDocumento(documento)) {
            throw new ReglaNegocioException("Ya existe un paciente con ese documento.");
        }
        Paciente paciente = new Paciente();
        aplicar(paciente, req, documento);
        return pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El paciente no existe."));
    }

    @Transactional
    public Paciente actualizar(Long id, PacienteRequest req) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El paciente no existe."));
        String documento = req.documento().trim();
        if (pacienteRepository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ReglaNegocioException("Ya existe otro paciente con ese documento.");
        }
        aplicar(paciente, req, documento);
        return pacienteRepository.save(paciente);
    }

    private void aplicar(Paciente paciente, PacienteRequest req, String documento) {
        paciente.setNombre(req.nombre().trim());
        paciente.setApellido(req.apellido().trim());
        paciente.setDocumento(documento);
        paciente.setCorreo(req.correo());
        paciente.setTelefono(req.telefono());
        paciente.setFechaNacimiento(req.fechaNacimiento());
    }
}
