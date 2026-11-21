package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.exception.ReglaNegocioException;
import com.clinica.VitalisSV.model.*;
import com.clinica.VitalisSV.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceReprogramarTest {

    @Mock private CitaRepository citaRepository;
    @Mock private PacienteRepository pacienteRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private HorarioDisponibleRepository horarioRepository;

    @InjectMocks private CitaService citaService;

    private Medico medico;
    private HorarioDisponible horarioLunes; // lunes 08:00 - 12:00
    private LocalDateTime lunes10;
    private Cita cita;

    @BeforeEach
    void preparar() {
        medico = new Medico();
        medico.setId(2L);

        horarioLunes = new HorarioDisponible();
        horarioLunes.setMedico(medico);
        horarioLunes.setDiaSemana((byte) 1);
        horarioLunes.setHoraInicio(LocalTime.of(8, 0));
        horarioLunes.setHoraFin(LocalTime.of(12, 0));

        lunes10 = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10).withMinute(0).withSecond(0).withNano(0);

        cita = new Cita();
        cita.setMedico(medico);
        cita.setInicio(lunes10);
        cita.setFin(lunes10.plusMinutes(30));
        cita.setEstado(EstadoCita.PENDIENTE);
    }

    @Test
    @DisplayName("Reprograma una cita a una franja libre dentro del horario")
    void reprogramaCitaValida() {
        LocalDateTime nuevoInicio = lunes10.plusHours(1);
        LocalDateTime nuevoFin = nuevoInicio.plusMinutes(30);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));
        when(citaRepository.existsByMedicoIdAndIdNotAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                2L, 5L, EstadoCita.CANCELADA, nuevoFin, nuevoInicio)).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.reprogramar(5L, nuevoInicio, nuevoFin);

        assertEquals(nuevoInicio, resultado.getInicio());
        assertEquals(nuevoFin, resultado.getFin());
        assertEquals(EstadoCita.PENDIENTE, resultado.getEstado());
    }

    @Test
    @DisplayName("Rechaza reprogramar una cita cancelada")
    void rechazaCitaCancelada() {
        cita.setEstado(EstadoCita.CANCELADA);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class,
                () -> citaService.reprogramar(5L, lunes10.plusHours(1), lunes10.plusHours(1).plusMinutes(30)));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza reprogramar una cita completada")
    void rechazaCitaCompletada() {
        cita.setEstado(EstadoCita.COMPLETADA);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class,
                () -> citaService.reprogramar(5L, lunes10.plusHours(1), lunes10.plusHours(1).plusMinutes(30)));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza reprogramar una cita que no existe")
    void rechazaCitaInexistente() {
        when(citaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ReglaNegocioException.class,
                () -> citaService.reprogramar(5L, lunes10, lunes10.plusMinutes(30)));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza reprogramar fuera del horario del medico")
    void rechazaFueraDeHorario() {
        LocalDateTime nuevoInicio = lunes10.withHour(13);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> citaService.reprogramar(5L, nuevoInicio, nuevoInicio.plusMinutes(30)));
        assertTrue(ex.getMessage().contains("no atiende"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza reprogramar a una franja que choca con otra cita")
    void rechazaChoque() {
        LocalDateTime nuevoInicio = lunes10.plusHours(1);
        LocalDateTime nuevoFin = nuevoInicio.plusMinutes(30);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));
        when(citaRepository.existsByMedicoIdAndIdNotAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                2L, 5L, EstadoCita.CANCELADA, nuevoFin, nuevoInicio)).thenReturn(true);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> citaService.reprogramar(5L, nuevoInicio, nuevoFin));
        assertTrue(ex.getMessage().contains("ya tiene una cita"));
        verify(citaRepository, never()).save(any());
    }
}
