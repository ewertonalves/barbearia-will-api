package com.whatsapp.barbeariaWill.adapter.in;

import com.whatsapp.barbeariaWill.application.dto.WebhookMessage;
import com.whatsapp.barbeariaWill.application.dto.WebhookMessageParser;
import com.whatsapp.barbeariaWill.application.dto.WebhookPayload;
import com.whatsapp.barbeariaWill.application.useCase.*;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;
import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.SpringDataAppointmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebhookControllerTest {

    @Mock
    private IniciarAgendamentoUseCase iniciarUC;

    @Mock
    private EscolherServicoUseCase escolherServicoUC;

    @Mock
    private EscolherProfissionalUseCase escolherProfissionalUC;

    @Mock
    private EscolherDataUseCase escolherDataUC;

    @Mock
    private EscolherHorarioUseCase escolherHorarioUC;

    @Mock
    private ConfirmarAgendamentoUseCase confirmarUC;

    @Mock
    private WhatsAppClientIntefacePort client;

    @Mock
    private SpringDataAppointmentRepository appointmentRepository;

    @InjectMocks
    private WebhookController controller;

    private final static String FROM = "5511999999999";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /** Cria um payload com apenas texto livre, sem interactive */
    private WebhookPayload payloadComTexto(String texto) {
        var text    = new WebhookPayload.Text(texto);
        var msg     = new WebhookPayload.Message(FROM, text, null);
        var value   = new WebhookPayload.Value(List.of(msg));
        var change  = new WebhookPayload.Change(value);
        var entry   = new WebhookPayload.Entry(List.of(change));
        return new WebhookPayload(List.of(entry));
    }

    /** Cria um payload com interactive list_reply */
    private WebhookPayload payloadComListReply() {
        var listReply   = new WebhookPayload.ListReply("servico_corte_cabelo", "title");
        var interactive = new WebhookPayload.Interactive("list_reply", listReply);
        var msg         = new WebhookPayload.Message(FROM, null, interactive);
        var value       = new WebhookPayload.Value(List.of(msg));
        var change      = new WebhookPayload.Change(value);
        var entry       = new WebhookPayload.Entry(List.of(change));
        return new WebhookPayload(List.of(entry));
    }

    @Test
    void deveChamarIniciarQuandoEstagioInciado() {
        var p = payloadComTexto("Olá");
        ResponseEntity<Void> resp = controller.receber(p);
        assertEquals(200, resp.getStatusCode().value());
        verify(iniciarUC).execute(FROM);
        verifyNoMoreInteractions(
                escolherServicoUC, escolherProfissionalUC,
                escolherDataUC, escolherHorarioUC, confirmarUC, client
        );
    }

    @Test
    void deveChamarEscolherServicoQuandoEstagioServicoSelecionado() {
        var p = payloadComListReply();
        ResponseEntity<Void> resp = controller.receber(p);
        assertEquals(200, resp.getStatusCode().value());
        verify(escolherServicoUC).execute(FROM, "servico_corte_cabelo");
        verifyNoMoreInteractions(
                iniciarUC, escolherProfissionalUC,
                escolherDataUC, escolherHorarioUC, confirmarUC, client
        );
    }

    @Test
    void deveChamarEscolherProfissionalQuandoEstagioProfissionalSelecionado() {
        var p = payloadComTexto("Abner");
        WebhookMessage mockedMsg = mock(WebhookMessage.class);
        when(mockedMsg.from()).thenReturn(FROM);
        when(mockedMsg.getEstagioAtual()).thenReturn(Status.PROFISSIONAL_SELECIONADO);
        when(mockedMsg.listReplyId()).thenReturn(null);
        when(mockedMsg.texto()).thenReturn("Abner");

        try (MockedStatic<WebhookMessageParser> wp = mockStatic(WebhookMessageParser.class)) {
            wp.when(() -> WebhookMessageParser.parse(p))
                    .thenReturn(mockedMsg);

            ResponseEntity<Void> resp = controller.receber(p);
            assertEquals(200, resp.getStatusCode().value());
            verify(escolherProfissionalUC).execute(FROM, "Abner");
            verifyNoMoreInteractions(iniciarUC, escolherServicoUC,
                    escolherDataUC, escolherHorarioUC, confirmarUC, client);
        }
    }

    @Test
    void deveChamarEscolherDataQuandoEstagioDataSelecionada() {
        var p = payloadComTexto("20/12/2025");
        WebhookMessage mockedMsg = mock(WebhookMessage.class);
        when(mockedMsg.from()).thenReturn(FROM);
        when(mockedMsg.getEstagioAtual()).thenReturn(Status.DATA_SELECIONADA);
        when(mockedMsg.texto()).thenReturn("20/12/2025");

        try (MockedStatic<WebhookMessageParser> wp = mockStatic(WebhookMessageParser.class)) {
            wp.when(() -> WebhookMessageParser.parse(p))
                    .thenReturn(mockedMsg);

            controller.receber(p);
            verify(escolherDataUC).execute(FROM, "20/12/2025");
        }
    }

    @Test
    void deveChamarEscolherHorarioQuandoEstagioHoraSelecionada() {
        var p = payloadComTexto("15:30");
        WebhookMessage mockedMsg = mock(WebhookMessage.class);
        when(mockedMsg.from()).thenReturn(FROM);
        when(mockedMsg.getEstagioAtual()).thenReturn(Status.HORA_SELECIONADA);
        when(mockedMsg.isConfirmacaoPositiva()).thenReturn(true);

        try (MockedStatic<WebhookMessageParser> wp = mockStatic(WebhookMessageParser.class)) {
            wp.when(() -> WebhookMessageParser.parse(p))
                    .thenReturn(mockedMsg);

            controller.receber(p);
            verify(escolherHorarioUC).execute(FROM, String.valueOf(true));
        }
    }

    @Test
    void deveChamarConfirmarQuandoEstagioConfirmado() {
        var p = payloadComTexto("sim");
        WebhookMessage mockedMsg = mock(WebhookMessage.class);
        when(mockedMsg.from()).thenReturn(FROM);
        when(mockedMsg.getEstagioAtual()).thenReturn(Status.CONFIRMADO);

        try (MockedStatic<WebhookMessageParser> wp = mockStatic(WebhookMessageParser.class)) {
            wp.when(() -> WebhookMessageParser.parse(p))
                    .thenReturn(mockedMsg);

            controller.receber(p);
            verify(confirmarUC).execute(FROM, true);
        }
    }

    @Test
    void deveUsarFallbackQuandoEstagioDesconhecido() {
        var p = payloadComTexto("???");
        WebhookMessage mockedMsg = mock(WebhookMessage.class);
        when(mockedMsg.from()).thenReturn(FROM);
        when(mockedMsg.getEstagioAtual()).thenReturn(null);

        try (MockedStatic<WebhookMessageParser> wp = mockStatic(WebhookMessageParser.class)) {
            wp.when(() -> WebhookMessageParser.parse(p))
                    .thenReturn(mockedMsg);
            controller.receber(p);
            verify(client).enviarTexto(eq(FROM), contains("Desculpe, não entendi"));
            verify(iniciarUC).execute(FROM);
        }
    }

    @Test
    void deveRetornarListaDeAgendamentosQuandoSucesso() {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setTelefone(FROM);
        List<AppointmentEntity> entities = List.of(entity);
        when(appointmentRepository.findAll()).thenReturn(entities);

        ResponseEntity<List<Appointment>> response = controller.listarAgendamentos();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(FROM, response.getBody().get(0).getTelefone());
        verify(appointmentRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumAgendamento() {
        when(appointmentRepository.findAll()).thenReturn(List.of());

        ResponseEntity<List<Appointment>> response = controller.listarAgendamentos();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(), response.getBody());
        verify(appointmentRepository).findAll();
    }
}
