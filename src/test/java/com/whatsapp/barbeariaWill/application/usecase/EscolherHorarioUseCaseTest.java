package com.whatsapp.barbeariaWill.application.usecase;

import com.whatsapp.barbeariaWill.application.useCase.EscolherHorarioUseCase;
import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EscolherHorarioUseCaseTest {

    @Mock
    private AppointmentInterfacePort repo;

    @Mock
    private WhatsAppClientIntefacePort client;

    @InjectMocks
    private EscolherHorarioUseCase useCase;

    private static final String TELEFONE        = "5511999990000";
    private static final String HORA_VALIDA     = "14:30";
    private static final String HORA_INVALIDA   = "25:61";

    private Appointment agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamento = new Appointment(TELEFONE);
        agendamento.definirServico("servico_x");
        agendamento.definirProfissional("Abner");
        agendamento.definirData(LocalDate.of(2025, 5, 20));
        assertEquals(Status.DATA_SELECIONADA, agendamento.getStatus());
    }

    @Test
    void deveDefinirHoraSalvarEChamarEnviarConfirmacaoQuandoTudoValido() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));
        useCase.execute(TELEFONE, HORA_VALIDA);

        verify(repo).buscarPorTelefone(TELEFONE);
        verify(repo).salvar(agendamento);

        String esperado = String.format(
                "Você escolheu: %s em %s às %s. Confirma? (Sim/Não)",
                agendamento.getServicoId(),
                agendamento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                HORA_VALIDA
        );

        verify(client).enviarConfirmacao(TELEFONE, esperado);
        verifyNoMoreInteractions(repo, client);

        assertEquals(LocalTime.of(14, 30), agendamento.getHora());
        assertEquals(Status.HORA_SELECIONADA, agendamento.getStatus());
    }

    @Test
    void quandoNaoExistirAgendamento_deveLancarIllegalArgumentException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(TELEFONE, HORA_VALIDA)
        );
        assertEquals("Agendamento não encontrado para " + TELEFONE, ex.getMessage());

        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }

    @Test
    void quandoHoraInvalida_devePropagarDateTimeParseException() {
        when(repo.buscarPorTelefone(TELEFONE)).thenReturn(Optional.of(agendamento));

        assertThrows(
                DateTimeParseException.class,
                () -> useCase.execute(TELEFONE, HORA_INVALIDA)
        );

        verify(repo).buscarPorTelefone(TELEFONE);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(client);
    }

}
