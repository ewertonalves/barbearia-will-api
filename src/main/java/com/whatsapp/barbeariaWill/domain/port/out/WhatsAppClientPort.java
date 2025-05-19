package com.whatsapp.barbeariaWill.domain.port.out;

public interface WhatsAppClientPort {

    void enviarListaServicos        (String telefone);
    void enviarListaProfissionais   (String telefone);
    void enviarListaDatas           (String telefone);
    void enviarListaHorarios        (String telefone, String data);
    void enviarConfirmacao          (String telefone, String resumo);
    void enviarTexto                (String telefone, String texto);
}
