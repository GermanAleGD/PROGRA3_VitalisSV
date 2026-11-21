package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.dto.PacienteRequest;
import com.clinica.VitalisSV.exception.RecursoNoEncontradoException;
import com.clinica.VitalisSV.exception.ReglaNegocioException;
import com.clinica.VitalisSV.model.Paciente;
import com.clinica.VitalisSV.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock private PacienteRepository pacienteRepository;
    @InjectMocks private PacienteService pacienteService;

    private PacienteRequest request(String documento) {
        return new PacienteRequest("Rosa", "Vega", documento, "rosa@correo.test",
                "7000-0099", LocalDate.of(1995, 8, 20));
    }

    @Test
    @DisplayName("Crea un paciente nuevo")
    void creaPaciente() {
        when(pacienteRepository.existsByDocumento("555")).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        Paciente p = pacienteService.crear(request(" 555 "));

        assertEquals("555", p.getDocumento());
        assertEquals("Rosa", p.getNombre());
    }

    @Test
    @DisplayName("Rechaza crear un paciente con documento repetido")
    void rechazaDocumentoRepetido() {
        when(pacienteRepository.existsByDocumento("555")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> pacienteService.crear(request("555")));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualiza los datos de un paciente")
    void actualizaPaciente() {
        Paciente existente = new Paciente();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pacienteRepository.existsByDocumentoAndIdNot("777", 1L)).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        Paciente p = pacienteService.actualizar(1L, request("777"));

        assertEquals("777", p.getDocumento());
        assertEquals("Vega", p.getApellido());
    }

    @Test
    @DisplayName("Rechaza actualizar con el documento de otro paciente")
    void rechazaActualizarConDocumentoAjeno() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(pacienteRepository.existsByDocumentoAndIdNot("777", 1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> pacienteService.actualizar(1L, request("777")));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar un paciente inexistente da 404")
    void actualizarInexistente() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pacienteService.actualizar(99L, request("777")));
    }
}
