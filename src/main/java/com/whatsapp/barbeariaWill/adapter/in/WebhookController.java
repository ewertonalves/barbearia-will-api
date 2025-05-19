package com.whatsapp.barbeariaWill.adapter.in;

import com.whatsapp.barbeariaWill.application.dto.WebhookMessage;
import com.whatsapp.barbeariaWill.application.dto.WebhookMessageParser;
import com.whatsapp.barbeariaWill.application.dto.WebhookPayload;
import com.whatsapp.barbeariaWill.application.useCase.*;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final IniciarAgendamentoUseCase     iniciarUC;
    private final EscolherServicoUseCase        escolherServicoUC;
    private final EscolherProfissionalUseCase   escolherProfissionalUC;
    private final EscolherDataUseCase           escolherDataUC;
    private final EscolherHorarioUseCase        escolherHorarioUC;
    private final ConfirmarAgendamentoUseCase   confirmarUC;
    private final WhatsAppClientPort            client;


    public WebhookController(IniciarAgendamentoUseCase  iniciarUC,
                             EscolherServicoUseCase     escolherServicoUC,
                             EscolherProfissionalUseCase escolherProfissionalUC,
                             EscolherDataUseCase        escolherDataUC,
                             EscolherHorarioUseCase     escolherHorarioUC,
                             ConfirmarAgendamentoUseCase confirmarUC,
                             WhatsAppClientPort client) {
        this.iniciarUC              = iniciarUC;
        this.escolherServicoUC      = escolherServicoUC;
        this.escolherProfissionalUC = escolherProfissionalUC;
        this.escolherDataUC         = escolherDataUC;
        this.escolherHorarioUC      = escolherHorarioUC;
        this.confirmarUC            = confirmarUC;
        this.client                 = client;
    }

    @PostMapping
    public ResponseEntity<Void> receber(@RequestBody WebhookPayload payload) {
        WebhookMessage msg = WebhookMessageParser.parse(payload);
        String telefone = msg.from();
        Status estagio  = msg.getEstagioAtual();

        if (estagio == null) {
            client.enviarTexto(
                    telefone,
                    "Desculpe, não entendi sua resposta. " +
                            "Para agendar um serviço, envie qualquer mensagem para reiniciar o atendimento."
            );
            iniciarUC.execute(telefone);
            return ResponseEntity.ok().build();
        }

        switch (msg.getEstagioAtual()) {
            case INICIADO ->
                    iniciarUC.execute(telefone);

            case SERVICO_SELECIONADO ->
                    escolherServicoUC.execute(telefone, msg.listReplyId());

            case PROFISSIONAL_SELECIONADO ->
                    escolherProfissionalUC.execute(telefone, msg.texto());

            case DATA_SELECIONADA ->
                    escolherDataUC.execute(telefone, msg.texto());

            case HORA_SELECIONADA ->
                    escolherHorarioUC.execute(telefone, String.valueOf(msg.isConfirmacaoPositiva()));

            case CONFIRMADO ->
                confirmarUC.execute(telefone, true);
        }

        return ResponseEntity.ok().build();
    }


}
