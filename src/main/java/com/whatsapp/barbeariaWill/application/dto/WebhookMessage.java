package com.whatsapp.barbeariaWill.application.dto;

import com.whatsapp.barbeariaWill.domain.enums.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public record WebhookMessage( String from, String texto, String listReplyId) {

    private static final Pattern DATA_PATTERN       = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
    private static final Pattern HORA_PATTERN       = Pattern.compile("\\d{2}:\\d{2}");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public Status getEstagioAtual() {
        if (listReplyId != null) {
            return Status.SERVICO_SELECIONADO;
        }
        if (texto != null) {
            var t = texto.trim().toLowerCase(Locale.ROOT);
            if (t.equals("sim") || t.equals("nao") || t.equals("não")) {
                return Status.CONFIRMADO;
            }
            if (HORA_PATTERN.matcher(texto).matches()) {
                return Status.HORA_SELECIONADA;
            }
            if (DATA_PATTERN.matcher(texto).matches()) {
                return Status.DATA_SELECIONADA;
            }
        }
        return Status.INICIADO;
    }

    public String getServicoId() {
        return getEstagioAtual() == Status.SERVICO_SELECIONADO ? listReplyId : null;
    }

    public String getData() {
        if (getEstagioAtual() != Status.DATA_SELECIONADA) return null;
        return String.valueOf(LocalDate.parse(texto.trim(), FMT_DATA));
    }

    public LocalTime getHora() {
        if (getEstagioAtual() != Status.HORA_SELECIONADA) return null;
        return LocalTime.parse(texto.trim(), FMT_HORA);
    }

    public boolean isConfirmacaoPositiva() {
        return getEstagioAtual() == Status.CONFIRMADO
                && texto.trim().equalsIgnoreCase("sim");
    }
}