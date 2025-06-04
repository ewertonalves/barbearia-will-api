package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.EscolherProfissionalUseCase;
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

public class EscolherProfissionalUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private EscolherProfissionalUseCase useCase;

    private static final String TELEFONE = "5511999990000";
    private static final String PROF_ID  = "Abner";

    private Appointment agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamento = new Appointment(TELEFONE);
        agendamento.definirServico("servico_x");
        assertEquals(Status.SERVICO_SELECIONADO, agendamento.getStatus());
    }

    @Test
    void deveDefinirProfissionalSalvarEChamarEnviarListaProfissionais() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));
        useCase.execute(TELEFONE, PROF_ID);

        verify(repo).buscarPorTelefone(TELEFONE);

        assertEquals(PROF_ID, agendamento.getProfissional());
        assertEquals(Status.PROFISSIONAL_SELECIONADO, agendamento.getStatus());

        verify(repo).salvar(agendamento);
        verify(client).enviarListaProfissionais(TELEFONE);
        verifyNoMoreInteractions(repo, client);
    }

    @Test
    void quandoNaoExistirAgendamento_deveLancarIllegalStateException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(TELEFONE, PROF_ID)
        );
        assertEquals(
                "Agendamento não encontrado para " + TELEFONE,
                ex.getMessage()
        );

        verify(repo).buscarPorTelefone(TELEFONE);
        verify(repo, never()).salvar(any());

        verifyNoInteractions(client);
        verifyNoMoreInteractions(repo);
    }

}
