package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.IniciarAgendamentoUseCase;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IniciarAgendamentoUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private IniciarAgendamentoUseCase useCase;

    private static final String TELEFONE = "5511999012345";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarNovoAgendamentoSalvarEChamarEnviarListaServicos() {
        useCase.execute(TELEFONE);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repo).salvar(captor.capture());
        Appointment saved = captor.getValue();

        assertNotNull(saved.getId(), "Deve gerar um UUID não-nulo");
        assertEquals(TELEFONE, saved.getTelefone(), "Telefone deve ser o mesmo passado");
        assertEquals(Status.INICIADO, saved.getStatus(), "Status inicial deve ser INICIADO");

        verify(client).enviarListaServicos(TELEFONE);

        verifyNoMoreInteractions(repo, client);
    }
}
