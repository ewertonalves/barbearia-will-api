package com.whatsapp.barbeariaWill.domain.port.out;

import com.whatsapp.barbeariaWill.domain.model.Appointment;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentInterfacePort {

    Appointment             salvar(Appointment appointment);
    Optional<Appointment>   buscarPorTelefone(String telefone);
    void                    remover(UUID id);
}
