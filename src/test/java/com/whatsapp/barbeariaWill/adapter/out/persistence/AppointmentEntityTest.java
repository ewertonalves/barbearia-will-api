package com.whatsapp.barbeariaWill.adapter.out.persistence;

import com.whatsapp.barbeariaWill.domain.enums.Status;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppointmentEntityTest {

    private static final String TELEFONE     = "5511999000000";
    private static final String SERVICO      = "servico_teste";
    private static final String PROFISSIONAL = "Abner";
    private static final LocalDate DATA  = LocalDate.of(2025, 12, 31);
    private static final LocalTime HORA  = LocalTime.of(9, 45);

    @Test
    void devePopularTodosOsCamposCorretamente() {
        Appointment domain = new Appointment(TELEFONE);
        domain.definirServico(SERVICO);
        domain.definirProfissional(PROFISSIONAL);
        domain.definirData(DATA);
        domain.definirHora(HORA);
        domain.confirmar();

        AppointmentEntity entity = new AppointmentEntity(domain);

        assertEquals(domain.getId(), entity.getId());
        assertEquals(TELEFONE, entity.getTelefone());
        assertEquals(SERVICO, entity.getServicoId());
        assertEquals(PROFISSIONAL, entity.getProfissional());
        assertEquals(DATA, entity.getData());
        assertEquals(HORA, entity.getHora());
        assertEquals(Status.CONFIRMADO, entity.getStatus());
    }

    @Test
    void deveRecriaDominioComMesmoEstadoQuandoConfirmado() throws  Exception {
        Appointment domainOrig = new Appointment(TELEFONE);
        domainOrig.definirServico(SERVICO);
        domainOrig.definirProfissional(PROFISSIONAL);
        domainOrig.definirData(DATA);
        domainOrig.definirHora(HORA);
        domainOrig.confirmar();
        AppointmentEntity entity = new AppointmentEntity(domainOrig);

        Appointment recreated = entity.toDomain();

        assertEquals(TELEFONE, recreated.getTelefone());
        assertEquals(SERVICO, recreated.getServicoId());
        assertEquals(PROFISSIONAL, recreated.getProfissional());
        assertEquals(DATA, recreated.getData());
        assertEquals(HORA, recreated.getHora());
        assertEquals(Status.CONFIRMADO, recreated.getStatus());

        Field idField = Appointment.class.getDeclaredField("id");
        idField.setAccessible(true);
        UUID idRecreated = (UUID) idField.get(recreated);
        assertEquals(domainOrig.getId(), idRecreated);
    }

    @Test
    void deveRecriaDominioComEstadoProfissionalQuandoNaoConfirmado() {
        Appointment domainOrig = new Appointment(TELEFONE);
        domainOrig.definirServico(SERVICO);
        domainOrig.definirProfissional(PROFISSIONAL);
        domainOrig.definirData(DATA);
        domainOrig.definirHora(HORA);
        AppointmentEntity entity = new AppointmentEntity(domainOrig);

        Appointment recreated = entity.toDomain();
        assertEquals(Status.PROFISSIONAL_SELECIONADO, recreated.getStatus());
    }
}
