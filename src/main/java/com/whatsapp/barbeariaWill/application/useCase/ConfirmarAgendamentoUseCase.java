package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentRepositoryPort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientPort;
import org.springframework.stereotype.Service;

@Service
public class ConfirmarAgendamentoUseCase {

    private final AppointmentRepositoryPort repo;
    private final WhatsAppClientPort        client;

    public ConfirmarAgendamentoUseCase(AppointmentRepositoryPort repo,
                                       WhatsAppClientPort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone, boolean confirmado) {
        var agendamento = repo
                .buscarPorTelefone(telefone)
                .orElseThrow(() -> new IllegalStateException("Agendamento não encontrado para " + telefone));

        if (confirmado) {
            agendamento.confirmar();
            repo.salvar(agendamento);
            client.enviarTexto(telefone, "✔️ Agendamento confirmado com sucesso!");
        } else {
            repo.remover(agendamento.getId());
            client.enviarTexto(telefone, "❌ Agendamento cancelado. Caso queira, inicie novamente.");
        }
    }
}