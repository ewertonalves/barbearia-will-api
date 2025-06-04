package com.whatsapp.barbeariaWill.adapter.out.persistence;

import com.whatsapp.barbeariaWill.adapter.out.persistence.repository.SpringDataAppointmentRepository;
import com.whatsapp.barbeariaWill.domain.model.Appointment;
import com.whatsapp.barbeariaWill.domain.model.AppointmentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SpringDataAppointmentRepositoryTest {

    @Autowired
    private SpringDataAppointmentRepository repository;

    private AppointmentEntity criarEntidadeCompleta(String telefone) {
        Appointment domain = new Appointment(telefone);
        domain.definirServico("servico_corte_cabelo");
        domain.definirProfissional("Abner");
        domain.definirData(LocalDate.of(2025, 12, 10));
        domain.definirHora(LocalTime.of(14,30));
        domain.confirmar();
        return new AppointmentEntity(domain);
    }

    @Test
    void deveSalvarEencontrarPorId() {
        AppointmentEntity entidade  = criarEntidadeCompleta("5511999999999");
        AppointmentEntity salvo     = repository.save(entidade);

        assertNotNull(salvo.getId(), "ID deve ser gerado pelo banco");
        Optional<AppointmentEntity> byId = repository.findById(salvo.getId());
        assertTrue(byId.isPresent(), "FindById deve encontrar a entidade salva");
        assertEquals("5511999999999", byId.get().getTelefone());
    }

    @Test
    void deveEncontrarTelefoneQuandoExiste() {
        String telefone = "5511999999999";
        AppointmentEntity entidade = criarEntidadeCompleta(telefone);
        repository.save(entidade);

        Optional<AppointmentEntity> resultado = repository.findByTelefone(telefone);
        assertTrue(resultado.isPresent(), "FindByTelefone deve retornar a entidade");
        assertEquals(telefone, resultado.get().getTelefone());
        assertEquals(entidade.getServicoId(), resultado.get().getServicoId());
        assertEquals(entidade.getProfissional(), resultado.get().getProfissional());
        assertEquals(entidade.getData(), resultado.get().getData());
        assertEquals(entidade.getHora(), resultado.get().getHora());
        assertEquals(entidade.getStatus(), resultado.get().getStatus());
    }

    @Test
    void deveBuscarTelefoneQuandoNaoExiste() {
        Optional<AppointmentEntity> resultado = repository.findByTelefone("000000");
        assertTrue(resultado.isEmpty(), "findByTelefone deve retornar Optional.empty se não achar");
    }

    @Test
    void deveRemoverEntidadePorId() {
        AppointmentEntity entidate  = criarEntidadeCompleta("5511999999999");
        AppointmentEntity salvo     = repository.save(entidate);

        UUID id = salvo.getId();
        assertTrue(repository.findById(id).isPresent());

        repository.deleteById(id);
        assertTrue(repository.findById(id).isEmpty(), "Após deleteById, entidade não deve mais existir");
    }

    @Test
    void deveRetornarTodasAsEntidadesSalvas() {
        AppointmentEntity e1 = criarEntidadeCompleta("5511999000004");
        AppointmentEntity e2 = criarEntidadeCompleta("5511999000005");
        repository.save(e1);
        repository.save(e2);

        var todos = repository.findAll();
        assertEquals(2, todos.size(), "findAll deve retornar todas as entidades salvas");
        assertTrue(todos.stream().anyMatch(e -> e.getTelefone().equals("5511999000004")));
        assertTrue(todos.stream().anyMatch(e -> e.getTelefone().equals("5511999000005")));
    }
}
