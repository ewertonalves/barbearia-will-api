package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.springframework.stereotype.Service;

@Service
public class IniciarAgendamentoUseCase {

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort        client;

    public IniciarAgendamentoUseCase(AppointmentInterfacePort repo,
                                     WhatsAppClientIntefacePort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone) {
        Appointment agendamento = new Appointment(telefone);
        repo.salvar(agendamento);
        client.enviarListaServicos(telefone);
    }
}
