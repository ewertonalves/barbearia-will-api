package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.ConfirmarAgendamentoUseCase;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfirmarAgendamentoUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private ConfirmarAgendamentoUseCase useCase;

    private static final String TELEFONE = "5511999990000";
    private Appointment agendamento;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        agendamento = new Appointment(TELEFONE);
        agendamento.definirServico("servico_teste");
        agendamento.definirProfissional("Abner");
        agendamento.definirData(java.time.LocalDate.of(2025, 5, 20));
        agendamento.definirHora(java.time.LocalTime.of(10, 0));
        assertEquals(Status.HORA_SELECIONADA, agendamento.getStatus());
    }

    @Test
    void deveSalvarEEnviarMensagemDeSucesso() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));
        useCase.execute(TELEFONE, true);
        assertEquals(Status.CONFIRMADO, agendamento.getStatus());
        verify(repo).salvar(agendamento);
        verify(client).enviarTexto(TELEFONE, "✔️ Agendamento confirmado com sucesso!");
        verify(repo, never()).remover(any());
    }

    @Test
    void deveRemoverEEnviarMensagemDeCancelamento() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));

        UUID idAntes = agendamento.getId();
        useCase.execute(TELEFONE, false);

        assertNotEquals(Status.CONFIRMADO, agendamento.getStatus());

        verify(repo).remover(idAntes);
        verify(client).enviarTexto(
                TELEFONE,
                "❌ Agendamento cancelado. Caso queira, inicie novamente."
        );
        verify(repo, never()).salvar(any());
    }

    @Test
    void deveLancarIllegalStateException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(TELEFONE, true)
        );
        assertEquals(
                "Agendamento não encontrado para " + TELEFONE,
                ex.getMessage()
        );
        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }

}
