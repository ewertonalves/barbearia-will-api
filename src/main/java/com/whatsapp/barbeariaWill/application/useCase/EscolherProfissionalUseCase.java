package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.springframework.stereotype.Service;

@Service
public class EscolherProfissionalUseCase {

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort        client;

    public EscolherProfissionalUseCase(AppointmentInterfacePort repo,
                                       WhatsAppClientIntefacePort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone, String profissionalId) {
        var agendamento = repo.buscarPorTelefone(telefone)
                .orElseThrow(() -> new IllegalStateException("Agendamento não encontrado para " + telefone));

        agendamento.definirProfissional(profissionalId);
        repo.salvar(agendamento);

        client.enviarListaProfissionais(telefone);
    }
}
