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
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private PacienteRepository pacienteRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private HorarioDisponibleRepository horarioRepository;

    @InjectMocks private CitaService citaService;

    private Paciente paciente;
    private Medico medico;
    private HorarioDisponible horarioLunes; // lunes 08:00 - 12:00
    private LocalDateTime lunes10;          // proximo lunes a las 10:00

    @BeforeEach
    void preparar() {
        paciente = new Paciente();
        medico = new Medico();

        horarioLunes = new HorarioDisponible();
        horarioLunes.setMedico(medico);
        horarioLunes.setDiaSemana((byte) 1);
        horarioLunes.setHoraInicio(LocalTime.of(8, 0));
        horarioLunes.setHoraFin(LocalTime.of(12, 0));

        lunes10 = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    @DisplayName("Agenda una cita valida dentro del horario y sin choques")
    void agendaCitaValida() {
        LocalDateTime fin = lunes10.plusMinutes(30);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));
        when(citaRepository.existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                2L, EstadoCita.CANCELADA, fin, lunes10)).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita cita = citaService.agendar(1L, 2L, lunes10, fin, "Control");

        assertEquals(EstadoCita.PENDIENTE, cita.getEstado());
        assertEquals(lunes10, cita.getInicio());
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("Rechaza una cita cuyo fin es anterior al inicio")
    void rechazaFinAnteriorAlInicio() {
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> citaService.agendar(1L, 2L, lunes10, lunes10.minusMinutes(30), "x"));
        assertTrue(ex.getMessage().contains("fin"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza una cita en el pasado")
    void rechazaCitaEnElPasado() {
        LocalDateTime ayer = LocalDateTime.now().minusDays(1);
        assertThrows(ReglaNegocioException.class,
                () -> citaService.agendar(1L, 2L, ayer, ayer.plusMinutes(30), "x"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza si el paciente no existe")
    void rechazaPacienteInexistente() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReglaNegocioException.class,
                () -> citaService.agendar(1L, 2L, lunes10, lunes10.plusMinutes(30), "x"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza una cita fuera del horario del medico")
    void rechazaFueraDeHorario() {
        LocalDateTime inicio = lunes10.withHour(13); // el medico atiende hasta las 12:00
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> citaService.agendar(1L, 2L, inicio, inicio.plusMinutes(30), "x"));
        assertTrue(ex.getMessage().contains("no atiende"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza una cita que choca con otra del mismo medico")
    void rechazaChoqueDeHorario() {
        LocalDateTime fin = lunes10.plusMinutes(30);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(2L, (byte) 1)).thenReturn(List.of(horarioLunes));
        when(citaRepository.existsByMedicoIdAndEstadoNotAndInicioLessThanAndFinGreaterThan(
                2L, EstadoCita.CANCELADA, fin, lunes10)).thenReturn(true);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> citaService.agendar(1L, 2L, lunes10, fin, "x"));
        assertTrue(ex.getMessage().contains("ya tiene una cita"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancela una cita pendiente")
    void cancelaCita() {
        Cita cita = new Cita();
        cita.setEstado(EstadoCita.PENDIENTE);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.cancelar(5L);

        assertEquals(EstadoCita.CANCELADA, resultado.getEstado());
    }

    @Test
    @DisplayName("No permite cancelar una cita completada")
    void noCancelaCitaCompletada() {
        Cita cita = new Cita();
        cita.setEstado(EstadoCita.COMPLETADA);
        when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class, () -> citaService.cancelar(5L));
        verify(citaRepository, never()).save(any());
    }
}
