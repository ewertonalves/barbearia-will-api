package com.whatsapp.barbeariaWill.domain.model;

import com.whatsapp.barbeariaWill.domain.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "appointment")
public class AppointmentEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "servico_id", length = 50)
    private String servicoId;

    @Column(name = "data_agendamento")
    private LocalDate data;

    @Column(name = "hora_agendamento")
    private LocalTime hora;

    @Column(name = "profissional")
    private String profissional;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private Status status;

    public AppointmentEntity(Appointment domain) {
        this.id             = domain.getId();
        this.telefone       = domain.getTelefone();
        this.servicoId      = domain.getServicoId();
        this.data           = domain.getData();
        this.hora           = domain.getHora();
        this.profissional   = domain.getProfissional();
        this.status         = domain.getStatus();
    }

    public Appointment toDomain() {
        Appointment domain = new Appointment(this.telefone);
        // ajustar id e estado
        try {
            var idField = Appointment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(domain, this.id);
        } catch (Exception ignored) {}
        domain.definirServico(this.servicoId);
        domain.definirData(this.data);
        domain.definirHora(this.hora);
        domain.definirProfissional(this.profissional);
        if (this.status == Status.CONFIRMADO) {
            domain.confirmar();
        }
        return domain;
    }
}
