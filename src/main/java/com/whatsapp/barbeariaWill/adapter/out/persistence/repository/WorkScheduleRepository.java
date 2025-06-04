package com.whatsapp.barbeariaWill.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.whatsapp.barbeariaWill.domain.model.WorkScheduleEntity;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkScheduleEntity, Long> {
    
    Optional<WorkScheduleEntity> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);
} 