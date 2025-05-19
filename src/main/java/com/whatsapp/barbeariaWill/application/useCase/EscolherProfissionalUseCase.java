package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentRepositoryPort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientPort;
import org.springframework.stereotype.Service;

@Service
public class EscolherProfissionalUseCase {

    private final AppointmentRepositoryPort repo;
    private final WhatsAppClientPort        client;

    public EscolherProfissionalUseCase(AppointmentRepositoryPort repo,
                                       WhatsAppClientPort client) {
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
