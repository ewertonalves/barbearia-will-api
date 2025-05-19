package com.whatsapp.barbeariaWill.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayload(@JsonProperty("entry") List<Entry> entries) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(@JsonProperty("changes") List<Change> changes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Change( @JsonProperty("value") Value value ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value( @JsonProperty("messages") List<Message> messages) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(@JsonProperty("from") String from,
            @JsonProperty("text") Text text,
            @JsonProperty("interactive") Interactive interactive) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text( @JsonProperty("body") String body ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Interactive( @JsonProperty("type") String type,
            @JsonProperty("list_reply") ListReply listReply ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListReply( @JsonProperty("id") String id,
            @JsonProperty("title") String title ) {
    }
}