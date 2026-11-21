package com.clinica.VitalisSV.service;

import com.clinica.VitalisSV.exception.RecursoNoEncontradoException;
import com.clinica.VitalisSV.model.Cita;
import com.clinica.VitalisSV.model.EstadoCita;
import com.clinica.VitalisSV.repository.CitaRepository;
import com.clinica.VitalisSV.repository.MedicoRepository;
import com.clinica.VitalisSV.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private PacienteRepository pacienteRepository;

    @InjectMocks private AgendaService agendaService;

    @Test
    @DisplayName("Agenda del medico en un dia: consulta el rango del dia y excluye canceladas")
    void agendaDeUnDia() {
        LocalDate fecha = LocalDate.of(2030, 1, 7);
        LocalDateTime desde = fecha.atStartOfDay();
        Cita cita = new Cita();
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.findByMedicoIdAndEstadoNotAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
                1L, EstadoCita.CANCELADA, desde, desde.plusDays(1))).thenReturn(List.of(cita));

        List<Cita> resultado = agendaService.agendaDeMedico(1L, fecha);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Agenda del medico sin fecha: devuelve todas las no canceladas")
    void agendaSinFecha() {
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.findByMedicoIdAndEstadoNotOrderByInicioAsc(1L, EstadoCita.CANCELADA))
                .thenReturn(List.of(new Cita(), new Cita()));

        assertEquals(2, agendaService.agendaDeMedico(1L, null).size());
    }

    @Test
    @DisplayName("Agenda de un medico que no existe: error 404")
    void agendaMedicoInexistente() {
        when(medicoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> agendaService.agendaDeMedico(99L, null));
        verifyNoInteractions(citaRepository);
    }

    @Test
    @DisplayName("Historial del paciente: devuelve sus citas")
    void historialDePaciente() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.findByPacienteIdOrderByInicioDesc(1L)).thenReturn(List.of(new Cita()));

        assertEquals(1, agendaService.historialDePaciente(1L).size());
    }

    @Test
    @DisplayName("Historial de un paciente que no existe: error 404")
    void historialPacienteInexistente() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> agendaService.historialDePaciente(99L));
        verifyNoInteractions(citaRepository);
    }
}
