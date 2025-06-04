package com.whatsapp.barbeariaWill.adapter.out.whatsapp;

import com.whatsapp.barbeariaWill.application.useCase.WorkScheduleUseCase;
import com.whatsapp.barbeariaWill.config.WhatsAppProperties;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WhatsAppClientAdapter implements WhatsAppClientIntefacePort {

    private final RestTemplate        restTemplate = new RestTemplate();
    private final WhatsAppProperties  props;
    private final WorkScheduleUseCase workScheduleService;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public WhatsAppClientAdapter(WhatsAppProperties props, 
                                WorkScheduleUseCase workScheduleService) {
        this.props               = props;
        this.workScheduleService = workScheduleService;
    }

    @Override
    public void enviarListaServicos(String telefone) {
        var rows = List.of(
                Map.of("id","servico_corte_cabelo",   "Serviço","Corte de Cabelo", "Valor","R$ 30,00"),
                Map.of("id","servico_corte_navalhado","Serviço","Corte Navalhado", "Valor","R$ 35,00"),
                Map.of("id","servico_corte_tesoura",  "Serviço","Corte na Tesoura","Valor","R$ 35,00"),
                Map.of("id","servico_perfil_cabelo",  "Serviço","Perfil do Cabelo","Valor","R$ 10,00"),
                Map.of("id","servico_sobrancelha",    "Serviço","Sobrancelha",     "Valor","R$ 10,00"),
                Map.of("id","servico_luzes",          "Serviço","Luzes",           "Valor","R$ 60,00"),
                Map.of("id","servico_alisamento",     "Serviço","Alisamento",      "Valor","R$ 25,00"),
                Map.of("id","servico_nevou",          "Serviço","Nevou",           "Valor","R$ 150,00"),
                Map.of("id","servico_barba",          "Serviço","Barba",           "Valor","R$ 25,00")
        );
        var payload = Map.<String, Object>of(
                "messaging_product","whatsapp",
                "to", telefone,
                "type","interactive",
                "interactive", Map.of(
                        "type","list",
                        "header", Map.of("type","text","text","Agendamento – Barbearia XYZ"),
                        "body", Map.of("text","Selecione o serviço:"),
                        "action", Map.of(
                                "button","Serviços",
                                "sections", List.of(Map.of("Serviço","Serviços Disponíveis","rows",rows))
                        )
                )
        );
        enviar(payload);
    }

    private void enviar(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(props.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(props.getUrl(), new HttpEntity<>(payload, headers), String.class);
    }

    @Override
    public void enviarListaProfissionais(String telefone) {
        var rows = List.of(
          Map.of("id", "Abner", "titulo", "Abner", "descrição", "Profissional"),
          Map.of("id", "Willian", "titulo", "Willian", "descrição", "Profissional")
        );
        var payload = Map.<String, Object>of(
                "messaging_product", "whatsapp",
                "to", telefone,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "list",
                        "header", Map.of("type", "text", "text", "Seleção de Profissional"),
                        "body",   Map.of("text", "Por favor, escolha o profissional:"),
                        "action", Map.of(
                                "button", "Profissionais",
                                "sections", List.of(
                                        Map.of("title", "Equipe", "rows", rows)
                                )
                        )
                )
        );
        enviar(payload);
    }

    @Override
    public void enviarListaDatas(String telefone) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (int i = 1; i<= 7; i++) {
            LocalDate data  = LocalDate.now().plusDays(i);
            String label    = data.format(FORMATO_DATA);
            rows.add(Map.of("id", label, "titulo", label, "descrição", ""));
        }
        var payload = Map.<String, Object>of(
                "messaging_product", "whatsapp",
                "to", telefone,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "list",
                        "header", Map.of("type", "text", "text", "Seleção de Data"),
                        "body",   Map.of("text", "Escolha a data do seu atendimento:"),
                        "action", Map.of(
                                "button", "Datas",
                                "sections", List.of(
                                        Map.of("title", "Próximos Dias", "rows", rows)
                                )
                        )
                )
        );
        enviar(payload);
    }

    @Override
    public void enviarListaHorarios(String telefone, String data) {
        LocalDate dataFormat = LocalDate.parse(data, FORMATO_DATA);
        List<Map<String, String>> rows = new ArrayList<>();
        

        var schedule = workScheduleService.getHorarioDeTrabalhoPorData(dataFormat);
        if (schedule.isPresent() && schedule.get().isDiaFalta()) {
            enviarTexto(telefone, "Desculpe, não há horários disponíveis para esta data. Por favor, escolha outra data.");
            return;
        }

        for (int hora = 8; hora <= 18; hora++) {
            LocalTime time = LocalTime.of(hora, 0);
            if (workScheduleService.isDisponivelParaAgendamento(dataFormat, time)) {
                String label = String.format("%02d:00", hora);
                rows.add(Map.of("id", label, "titulo", label, "descrição", ""));
            }
        }

        if (rows.isEmpty()) {
            enviarTexto(telefone, "Desculpe, não há horários disponíveis para esta data. Por favor," +
            " escolha outra data.");
            return;
        }

        var payload = Map.<String, Object>of(
                "messaging_product", "whatsapp",
                "to", telefone,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "list",
                        "header", Map.of("type", "text", "text", "Seleção de Horário"),
                        "body", Map.of("text", "Escolha o horário em " + data + ":"),
                        "action", Map.of(
                                "button", "Horários",
                                "sections", List.of(
                                        Map.of("title", "Turno", "rows", rows)
                                )
                        )
                )
        );
        enviar(payload);
    }

    @Override
    public void enviarConfirmacao(String telefone, String resumo) {
        enviarTexto(telefone, resumo);
    }

    @Override
    public void enviarTexto(String telefone, String texto) {
        var payload = Map.<String, Object>of(
                "messaging_product", "whatsapp",
                "to", telefone,
                "type", "text",
                "text", Map.of("body", texto)
        );
        enviar(payload);
    }

    @Override
    public void notificarBloqueioHorario(String telefone, String data, String horarioInicio, String horarioFim, String motivo) {
        String mensagem = String.format(
            "⚠️ *BLOQUEIO DE HORÁRIO*\n\n" +
            "Data: %s\n" +
            "Horário: %s - %s\n" +
            "Motivo: %s",
            data, horarioInicio, horarioFim, motivo
        );
        enviarTexto(telefone, mensagem);
    }
}
