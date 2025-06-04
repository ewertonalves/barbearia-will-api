package com.whatsapp.barbeariaWill.adapter.out.persistence;

import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.SpringDataAppointmentRepository;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AppointmentPersistenceAdapter implements AppointmentInterfacePort {

    private final SpringDataAppointmentRepository jpaRepo;

    public AppointmentPersistenceAdapter(SpringDataAppointmentRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Appointment salvar(Appointment appointment) {
        var entity = new AppointmentEntity(appointment);
        return jpaRepo.save(entity).toDomain();
    }

    @Override
    public Optional<Appointment> buscarPorTelefone(String telefone) {
        return jpaRepo.findByTelefone(telefone).map(AppointmentEntity::toDomain);
    }

    @Override
    public void remover(UUID id) {
        jpaRepo.deleteById(id);
    }
}
