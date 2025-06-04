package com.whatsapp.barbeariaWill.adapter.in;

import com.whatsapp.barbeariaWill.domain.model.WorkScheduleEntity;
import com.whatsapp.barbeariaWill.application.useCase.WorkScheduleUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedule")
public class WorkScheduleController {

   
    private final WorkScheduleUseCase workScheduleService;

    public WorkScheduleController(WorkScheduleUseCase workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping
    public ResponseEntity<WorkScheduleEntity> createSchedule(@RequestBody WorkScheduleEntity schedule) {
        try {
            WorkScheduleEntity created = workScheduleService.criarHorarioDeTrabalho(schedule);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkScheduleEntity> updateSchedule(@PathVariable Long id, @RequestBody WorkScheduleEntity schedule) {
        try {
            schedule.setId(id);
            WorkScheduleEntity updated = workScheduleService.atualizarHorarioDeTrabalho(schedule);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{date}")
    public ResponseEntity<WorkScheduleEntity> getScheduleByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        Optional<WorkScheduleEntity> schedule = workScheduleService.getHorarioDeTrabalhoPorData(localDate);
        return schedule.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        try {
            workScheduleService.deletarHorarioDeTrabalho(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 