package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EscolherDataUseCase {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/mm/yyyy");

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort        client;

    public EscolherDataUseCase(AppointmentInterfacePort repo,
                               WhatsAppClientIntefacePort client) {
        this.repo   = repo;
        this.client = client;
    }

    public void execute(String telefone, String data) {
        var agendamento = repo.buscarPorTelefone(telefone)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado para " + telefone));
        LocalDate dataFormat = LocalDate.parse(data, FORMATO_DATA);
        agendamento.definirData(dataFormat);
        repo.salvar(agendamento);
        client.enviarListaHorarios(telefone, data);
    }
}
