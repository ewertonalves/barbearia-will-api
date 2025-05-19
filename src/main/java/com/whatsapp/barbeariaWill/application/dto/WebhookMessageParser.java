package com.whatsapp.barbeariaWill.application.dto;

public final class WebhookMessageParser {

    public static WebhookMessage parse(WebhookPayload payload) {
        var entry   = requireOne(payload.entries(), "entries");
        var change  = requireOne(entry.changes(), "changes");
        var value   = change.value();
        var message = requireOne(value.messages(), "messages");

        String from      = message.from();
        String bodyText  = message.text() != null ? message.text().body() : null;
        var interactive  = message.interactive();
        String listReply = (interactive != null && "list_reply".equals(interactive.type()))
                ? interactive.listReply().id()
                : null;

        return new WebhookMessage(from, bodyText, listReply);
    }

    private static <T> T requireOne(java.util.List<T> list, String name) {
        if (list == null || list.size() != 1) {
            throw new IllegalArgumentException("Esperado exatamente 1 elemento em " + name);
        }
        return list.get(0);
    }
}
