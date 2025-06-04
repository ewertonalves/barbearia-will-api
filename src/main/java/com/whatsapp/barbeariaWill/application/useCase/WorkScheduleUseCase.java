package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.model.WorkScheduleEntity;
import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.WorkScheduleRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class WorkScheduleUseCase {

    private final WorkScheduleRepository workScheduleRepository;

    public WorkScheduleUseCase(WorkScheduleRepository workScheduleRepository) {
        this.workScheduleRepository = workScheduleRepository;
    }

    public WorkScheduleEntity criarHorarioDeTrabalho(WorkScheduleEntity schedule) {
        if (workScheduleRepository.existsByDate(schedule.getDate())) {
            throw new RuntimeException("Horario de trabalho já existe para esta data");
        }
        return workScheduleRepository.save(schedule);
    }

    public WorkScheduleEntity atualizarHorarioDeTrabalho(WorkScheduleEntity schedule) {
        if (!workScheduleRepository.existsById(schedule.getId())) {
            throw new RuntimeException("Horario de trabalho não encontrado");
        }
        return workScheduleRepository.save(schedule);
    }

    public Optional<WorkScheduleEntity> getHorarioDeTrabalhoPorData(LocalDate date) {
        return workScheduleRepository.findByDate(date);
    }

    public void deletarHorarioDeTrabalho(Long id) {
        workScheduleRepository.deleteById(id);
    }

    public boolean isDisponivelParaAgendamento(LocalDate date, LocalTime time) {
        return workScheduleRepository.findByDate(date)
            .map(schedule -> !schedule.isDiaFalta() && 
                (schedule.isDiaParcial() 
                    ? time.isAfter(schedule.getInicioHorario()) && time.isBefore(schedule.getFimHorario())
                    : time.isAfter(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(18, 0))))
            .orElse(false);
    }
} 