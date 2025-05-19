package com.whatsapp.barbeariaWill.domain.model;

import com.whatsapp.barbeariaWill.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Appointment {

    private final UUID   id;
    private final String telefone;
    private String       servicoId;
    private String       profissional;
    private LocalDate    data;
    private LocalTime    hora;
    private Status       status;

    public Appointment(String telefone) {
        this.id         = UUID.randomUUID();
        this.telefone   = telefone;
        this.status     = Status.INICIADO;
    }

    public void definirServico(String servicoId){
        this.servicoId  = servicoId;
        this.status     = Status.SERVICO_SELECIONADO;
    }

    public void definirProfissional(String profissional) {
        this.profissional = profissional;
        this.status       = Status.PROFISSIONAL_SELECIONADO;
    }

    public void definirData(LocalDate data) {
        this.data   = data;
        this.status = Status.DATA_SELECIONADA;
    }

    public void definirHora(LocalTime hora) {
        this.hora   = hora;
        this.status = Status.HORA_SELECIONADA;
    }

    public void confirmar() {
        this.status = Status.CONFIRMADO;
    }

}
