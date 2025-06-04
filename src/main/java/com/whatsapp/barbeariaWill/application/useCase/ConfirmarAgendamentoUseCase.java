package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.springframework.stereotype.Service;

@Service
public class ConfirmarAgendamentoUseCase {

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort        client;

    public ConfirmarAgendamentoUseCase(AppointmentInterfacePort repo,
                                       WhatsAppClientIntefacePort client) {
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