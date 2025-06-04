package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.springframework.stereotype.Service;

@Service
public class EscolherServicoUseCase {

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort        client;

    public EscolherServicoUseCase(AppointmentInterfacePort repo,
                                  WhatsAppClientIntefacePort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone, String servicoId) {
        Appointment agendamento = repo.buscarPorTelefone(telefone)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        agendamento.definirServico(servicoId);
        repo.salvar(agendamento);
        client.enviarListaDatas(telefone);

    }
}
