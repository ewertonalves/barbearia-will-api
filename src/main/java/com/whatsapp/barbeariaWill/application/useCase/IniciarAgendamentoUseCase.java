package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentRepositoryPort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientPort;
import org.springframework.stereotype.Service;

@Service
public class IniciarAgendamentoUseCase {

    private final AppointmentRepositoryPort repo;
    private final WhatsAppClientPort        client;

    public IniciarAgendamentoUseCase(AppointmentRepositoryPort repo,
                                     WhatsAppClientPort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone) {
        Appointment agendamento = new Appointment(telefone);
        repo.salvar(agendamento);
        client.enviarListaServicos(telefone);
    }
}
