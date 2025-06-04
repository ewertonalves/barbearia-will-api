package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.adapter.out.persistence.WorkSchedule;
import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.WorkScheduleRepository;
import com.whatsapp.barbeariaWill.application.useCase.WorkScheduleUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkScheduleUseCaseTest {

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @InjectMocks
    private WorkScheduleUseCase workScheduleService;

    private WorkSchedule workSchedule;
    private LocalDate testDate;
    private LocalTime testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDate     = LocalDate.now();
        testTime     = LocalTime.of(10, 0);
        workSchedule = new WorkSchedule();
        workSchedule.setId(1L);
        workSchedule.setDate(testDate);
        workSchedule.setInicioHorario(LocalTime.of(9, 0));
        workSchedule.setFimHorario(LocalTime.of(17, 0));
        workSchedule.setDiaFalta(false);
        workSchedule.setDiaParcial(false);
    }

    @Test
    void criarHorarioDeTrabalho_QuandoNaoExiste_DeveCriar() {
        when(workScheduleRepository.existsByDate(testDate)).thenReturn(false);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(workSchedule);

        WorkSchedule result = workScheduleService.criarHorarioDeTrabalho(workSchedule);

        assertNotNull(result);
        verify(workScheduleRepository).save(workSchedule);
    }

    @Test
    void criarHorarioDeTrabalho_QuandoJaExiste_DeveLancarExcecao() {
        when(workScheduleRepository.existsByDate(testDate)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            workScheduleService.criarHorarioDeTrabalho(workSchedule)
        );
    }

    @Test
    void atualizarHorarioDeTrabalho_QuandoExiste_DeveAtualizar() {
        when(workScheduleRepository.existsById(1L)).thenReturn(true);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(workSchedule);

        WorkSchedule result = workScheduleService.atualizarHorarioDeTrabalho(workSchedule);

        assertNotNull(result);
        verify(workScheduleRepository).save(workSchedule);
    }

    @Test
    void atualizarHorarioDeTrabalho_QuandoNaoExiste_DeveLancarExcecao() {
        when(workScheduleRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> 
            workScheduleService.atualizarHorarioDeTrabalho(workSchedule)
        );
    }

    @Test
    void getHorarioDeTrabalhoPorData_QuandoExiste_DeveRetornarHorario() {
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.of(workSchedule));

        Optional<WorkSchedule> result = workScheduleService.getHorarioDeTrabalhoPorData(testDate);

        assertTrue(result.isPresent());
        assertEquals(workSchedule, result.get());
    }

    @Test
    void getHorarioDeTrabalhoPorData_QuandoNaoExiste_DeveRetornarVazio() {
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.empty());

        Optional<WorkSchedule> result = workScheduleService.getHorarioDeTrabalhoPorData(testDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void deletarHorarioDeTrabalho_DeveChamarDelete() {
        workScheduleService.deletarHorarioDeTrabalho(1L);

        verify(workScheduleRepository).deleteById(1L);
    }

    @Test
    void isDisponivelParaAgendamento_QuandoDiaNormal_DeveRetornarTrue() {
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.of(workSchedule));

        boolean result = workScheduleService.isDisponivelParaAgendamento(testDate, testTime);

        assertTrue(result);
    }

    @Test
    void isDisponivelParaAgendamento_QuandoDiaFolga_DeveRetornarFalse() {
        workSchedule.setDiaFalta(true);
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.of(workSchedule));

        boolean result = workScheduleService.isDisponivelParaAgendamento(testDate, testTime);

        assertFalse(result);
    }

    @Test
    void isDisponivelParaAgendamento_QuandoDiaParcial_DeveRetornarTrueSeDentroDoHorario() {
        workSchedule.setDiaParcial(true);
        workSchedule.setInicioHorario(LocalTime.of(9, 0));
        workSchedule.setFimHorario(LocalTime.of(13, 0));
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.of(workSchedule));

        boolean result = workScheduleService.isDisponivelParaAgendamento(testDate, testTime);

        assertTrue(result);
    }

    @Test
    void isDisponivelParaAgendamento_QuandoDiaParcial_DeveRetornarFalseSeForaDoHorario() {
        workSchedule.setDiaParcial(true);
        workSchedule.setInicioHorario(LocalTime.of(9, 0));
        workSchedule.setFimHorario(LocalTime.of(13, 0));
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.of(workSchedule));

        boolean result = workScheduleService.isDisponivelParaAgendamento(testDate, LocalTime.of(14, 0));

        assertFalse(result);
    }

    @Test
    void isDisponivelParaAgendamento_QuandoNaoExisteHorario_DeveRetornarFalse() {
        when(workScheduleRepository.findByDate(testDate)).thenReturn(Optional.empty());

        boolean result = workScheduleService.isDisponivelParaAgendamento(testDate, testTime);

        assertFalse(result);
    }
} 