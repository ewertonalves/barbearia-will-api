package com.whatsapp.barbeariaWill.adapter.out.persistence;

import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.SpringDataAppointmentRepository;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppointmentPersistenceAdapterTest {

    @Mock
    private SpringDataAppointmentRepository jpaRepo;

    @InjectMocks
    private AppointmentPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void salvar_deveConverterEDepositarNoRepositorioERetornarDominio() {
        Appointment input    = new Appointment("5511999990000");
        Appointment expected = new Appointment("5511999990000");

        AppointmentEntity mockEntity = mock(AppointmentEntity.class);

        when(mockEntity.toDomain()).thenReturn(expected);
        when(jpaRepo.save(any(AppointmentEntity.class))).thenReturn(mockEntity);

        Appointment resultado = adapter.salvar(input);

        assertSame(expected, resultado, "Deve retornar o domínio vindo de toDomain()");
        ArgumentCaptor<AppointmentEntity> captor = ArgumentCaptor.forClass(AppointmentEntity.class);
        verify(jpaRepo).save(captor.capture());
        AppointmentEntity passed = captor.getValue();
        assertEquals(input.getTelefone(), passed.getTelefone());
        verifyNoMoreInteractions(jpaRepo);
    }

    @Test
    void deveBuscarPorTelefone_quandoEncontrarDeveRetornarOptionalComDominio() {
        String telefone              = "5511999990001";
        AppointmentEntity mockEntity = mock(AppointmentEntity.class);
        Appointment expected         = new Appointment(telefone);

        when(mockEntity.toDomain()).thenReturn(expected);
        when(jpaRepo.findByTelefone(telefone)).thenReturn(Optional.of(mockEntity));

        Optional<Appointment> opt = adapter.buscarPorTelefone(telefone);

        assertTrue(opt.isPresent(), "Deve encontrar o agendamento");
        assertSame(expected, opt.get(), "Deve retornar o domínio vindo de toDomain()");
        verify(jpaRepo).findByTelefone(telefone);
        verifyNoMoreInteractions(jpaRepo);
    }

    @Test
    void deveBuscarPorTelefone_quandoNaoEncontrarDeveRetornarOptionalVazio() {
        String telefone = "5511999990002";
        when(jpaRepo.findByTelefone(telefone)).thenReturn(Optional.empty());

        Optional<Appointment> opt = adapter.buscarPorTelefone(telefone);

        assertTrue(opt.isEmpty(), "Deve retornar Optional.empty quando não encontrar");
        verify(jpaRepo).findByTelefone(telefone);
        verifyNoMoreInteractions(jpaRepo);
    }

    @Test
    void remover_deveChamarDeleteByIdNoRepo() {
        UUID id = UUID.randomUUID();

        adapter.remover(id);

        verify(jpaRepo).deleteById(id);
        verifyNoMoreInteractions(jpaRepo);
    }

}
