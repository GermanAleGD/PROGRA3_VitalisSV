package com.clinica.VitalisSV.repository;

import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;
import com.clinica.VitalisSV.model.Medico;
import com.clinica.VitalisSV.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba la consulta de solapamiento contra MySQL real.
 * Requiere MySQL encendido y las migraciones aplicadas (usa los medicos de la V2).
 * Cada prueba se revierte al terminar (@Transactional), sin dejar datos.
 */
@SpringBootTest
@Transactional
class CitaRepositoryIntegrationTest {

    @Autowired private CitaRepository citaRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private PacienteRepository pacienteRepository;

    private Medico ana;
    private Medico carlos;

    // Cita existente de Ana: 2030-01-07 de 10:00 a 10:30
    private final LocalDateTime inicio = LocalDateTime.of(2030, 1, 7, 10, 0);
    private final LocalDateTime fin = LocalDateTime.of(2030, 1, 7, 10, 30);

    @BeforeEach
    void preparar() {
        ana = buscarMedico("ana.ramirez@vitalis.test");
        carlos = buscarMedico("carlos.lopez@vitalis.test");

        Paciente paciente = new Paciente();
        paciente.setNombre("Prueba");
        paciente.setApellido("Integracion");
        paciente.setDocumento("TEST-" + System.nanoTime());
        paciente = pacienteRepository.save(paciente);

        Cita existente = new Cita();
        existente.setPaciente(paciente);
        existente.setMedico(ana);
        existente.setInicio(inicio);
        existente.setFin(fin);
        existente.setEstado(EstadoCita.PENDIENTE);
        citaRepository.save(existente);
    }

    private Medico buscarMedico(String correo) {
        return medicoRepository.findAll().stream()
                .filter(m -> m.getCorreo().equals(correo))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Falta el medico de prueba " + correo));
    }

    private boolean hayChoque(Medico medico, LocalDateTime nuevoInicio, LocalDateTime nuevoFin) {
        return citaRepository.existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                medico.getId(), EstadoCita.CANCELADA, nuevoFin, nuevoInicio);
    }

    @Test
    @DisplayName("Detecta la misma franja exacta")
    void detectaMismaFranja() {
        assertTrue(hayChoque(ana, inicio, fin));
    }

    @Test
    @DisplayName("Detecta solapamiento parcial (10:15-10:45)")
    void detectaSolapamientoParcial() {
        assertTrue(hayChoque(ana, inicio.plusMinutes(15), fin.plusMinutes(15)));
    }

    @Test
    @DisplayName("Detecta una cita contenida dentro de otra (10:10-10:20)")
    void detectaCitaContenida() {
        assertTrue(hayChoque(ana, inicio.plusMinutes(10), fin.minusMinutes(10)));
    }

    @Test
    @DisplayName("No marca choque con una cita contigua posterior (10:30-11:00)")
    void permiteContiguaPosterior() {
        assertFalse(hayChoque(ana, fin, fin.plusMinutes(30)));
    }

    @Test
    @DisplayName("No marca choque con una cita contigua anterior (09:30-10:00)")
    void permiteContiguaAnterior() {
        assertFalse(hayChoque(ana, inicio.minusMinutes(30), inicio));
    }

    @Test
    @DisplayName("No marca choque con otro medico en la misma franja")
    void permiteOtroMedico() {
        assertFalse(hayChoque(carlos, inicio, fin));
    }

    @Test
    @DisplayName("Ignora las citas canceladas")
    void ignoraCitasCanceladas() {
        Cita cita = citaRepository.findAll().stream()
                .filter(c -> c.getMedico().getId().equals(ana.getId()) && c.getInicio().equals(inicio))
                .findFirst().orElseThrow();
        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);

        assertFalse(hayChoque(ana, inicio, fin));
    }
}
