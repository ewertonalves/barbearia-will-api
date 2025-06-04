package com.whatsapp.barbeariaWill.domain.port.out;

public interface WhatsAppClientIntefacePort {

    void enviarListaServicos        (String telefone);
    void enviarListaProfissionais   (String telefone);
    void enviarListaDatas           (String telefone);
    void enviarListaHorarios        (String telefone, String data);
    void enviarConfirmacao          (String telefone, String resumo);
    void enviarTexto                (String telefone, String texto);
    void notificarBloqueioHorario   (String telefone, String data, String horarioInicio, String horarioFim, String motivo);
}
