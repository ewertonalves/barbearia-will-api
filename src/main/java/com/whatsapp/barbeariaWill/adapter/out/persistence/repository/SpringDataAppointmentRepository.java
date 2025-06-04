package com.whatsapp.barbeariaWill.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    Optional<AppointmentEntity> findByTelefone(String telefone);
}