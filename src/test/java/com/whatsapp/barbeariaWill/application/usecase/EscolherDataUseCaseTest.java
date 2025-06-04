package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.EscolherDataUseCase;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EscolherDataUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private EscolherDataUseCase useCase;

    private static final String TELEFONE    = "5511999000000";
    private static final String DATA_STRING = "20/12/2025";

    private Appointment agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamento = new Appointment(TELEFONE);
        agendamento.definirServico("servico_x");
        assertEquals(Status.SERVICO_SELECIONADO, agendamento.getStatus());
    }

    @Test
    void quandoRepoEncontrarMasParsingFalhar_devePropagarDateTimeParseException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));
        assertThrows(
                DateTimeParseException.class,
                () -> useCase.execute(TELEFONE, DATA_STRING)
        );

        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }

    @Test
    void quandoNaoExistirAgendamento_deveLancarIllegalArgumentException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(TELEFONE, DATA_STRING)
        );
        assertEquals("Agendamento não encontrado para " + TELEFONE, ex.getMessage());

        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }
}
