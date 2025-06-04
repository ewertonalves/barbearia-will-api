package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.EscolherServicoUseCase;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EscolherServicoUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private EscolherServicoUseCase useCase;

    private static final String TELEFONE   = "5511999000000";
    private static final String SERVICO_ID = "servico_teste";

    private Appointment agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamento = new Appointment(TELEFONE);
        assertEquals(Status.INICIADO, agendamento.getStatus());
    }

    @Test
    void deveDefinirServicoSalvarEChamarEnviarListaDatas() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));

        useCase.execute(TELEFONE, SERVICO_ID);

        verify(repo).buscarPorTelefone(TELEFONE);

        assertEquals(SERVICO_ID, agendamento.getServicoId());
        assertEquals(Status.SERVICO_SELECIONADO, agendamento.getStatus());

        verify(repo).salvar(agendamento);
        verify(client).enviarListaDatas(TELEFONE);
        verifyNoMoreInteractions(repo, client);
    }

    @Test
    void quandoNaoExistirSessao_deveLancarIllegalArgumentException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(TELEFONE, SERVICO_ID)
        );
        assertEquals("Sessão não encontrada", ex.getMessage());

        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }

}
