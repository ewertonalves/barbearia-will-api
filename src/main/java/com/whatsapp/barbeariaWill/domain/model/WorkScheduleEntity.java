package com.whatsapp.barbeariaWill.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "work_schedules")
public class WorkScheduleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "inicio_horario")
    private LocalTime inicioHorario;

    @Column(name = "fim_horario")
    private LocalTime fimHorario;

    @Column(name = "is_dia_falta", nullable = false)
    private boolean isDiaFalta;

    @Column(name = "is_dia_parcial", nullable = false)
    private boolean isDiaParcial;

} 