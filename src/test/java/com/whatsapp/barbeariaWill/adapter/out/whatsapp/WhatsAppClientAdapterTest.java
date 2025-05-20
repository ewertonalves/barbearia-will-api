package com.whatsapp.barbeariaWill.adapter.out.whatsapp;

import com.whatsapp.barbeariaWill.config.WhatsAppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class WhatsAppClientAdapterTest {

    private static final String URL   = "https://graph.facebook.com/v15.0/123/messages";
    private static final String TOKEN = "TOKEN";
    private static final String PHONE = "5511999999999";

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private WhatsAppProperties props;

    private WhatsAppClientAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        props = new WhatsAppProperties();
        props.setUrl(URL);
        props.setToken(TOKEN);

        adapter = new WhatsAppClientAdapter(props);
        Field rtField = WhatsAppClientAdapter.class.getDeclaredField("restTemplate");
        rtField.setAccessible(true);
        rtField.set(adapter, mockRestTemplate);
    }

    private void verifyPost(Map<String, Object> expectedPayload) {
        // captura os argumentos passados ao RestTemplate
        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor =
                ArgumentCaptor.forClass((Class) HttpEntity.class);

        verify(mockRestTemplate).postForEntity(eq(URL), captor.capture(), eq(String.class));

        HttpEntity<Map<String, Object>> entity = captor.getValue();
        HttpHeaders headers = entity.getHeaders();

        // verifica cabeçalhos
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertTrue(headers.getFirst(HttpHeaders.AUTHORIZATION).contains(TOKEN));

        // verifica payload
        Map<String, Object> actual = entity.getBody();
        assertNotNull(actual);
        assertEquals(expectedPayload.get("messaging_product"), actual.get("messaging_product"));
        assertEquals(expectedPayload.get("to"), actual.get("to"));
        assertEquals(expectedPayload.get("type"), actual.get("type"));
        assertEquals(expectedPayload.get("interactive") != null,
                actual.get("interactive") != null);
        assertEquals(expectedPayload.get("text") != null,
                actual.get("text") != null);
    }

    @Test
    void deveEnviarTexto() {
        String msg = "Olá, mundo!";
        adapter.enviarTexto(PHONE, msg);

        Map<String, Object> expected = Map.of(
                "messaging_product", "whatsapp",
                "to", PHONE,
                "type", "text",
                "text", Map.of("body", msg)
        );
        verifyPost(expected);
    }

    @Test
    void deveEnviarConfirmacao_delegarParaEnviarTexto() {
        String resumo = "Resumo de teste";

        when(mockRestTemplate.postForEntity(
            eq(URL), any(HttpEntity.class), eq(String.class))
        ).thenReturn(ResponseEntity.ok(""));

        adapter.enviarConfirmacao(PHONE, resumo);
        Map<String, Object> expected = Map.of(
                "messaging_product", "whatsapp",
                "to", PHONE,
                "type", "text",
                "text", Map.of("body", resumo)
        );
        verifyPost(expected);
    }

    @Test
    void deveEnviarListaDeServicos() {
        adapter.enviarListaServicos(PHONE);

        Map<String, Object> expected = Map.of(
                "messaging_product", "whatsapp",
                "to", PHONE,
                "type", "interactive",
                "interactive", Collections.emptyMap()
        );
        verifyPost(expected);
    }

    @Test
    void deveEnviarListaDeProfissionais() {
        adapter.enviarListaProfissionais(PHONE);

        Map<String, Object> expected = Map.of(
                "messaging_product", "whatsapp",
                "to", PHONE,
                "type", "interactive",
                "interactive", Collections.emptyMap()
        );
        verifyPost(expected);
    }

    @Test
    void deveEnviarListaDeDatas_tem7Dias() {
        adapter.enviarListaDatas(PHONE);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor =
                ArgumentCaptor.forClass((Class) HttpEntity.class);
        verify(mockRestTemplate).postForEntity(eq(URL), captor.capture(), eq(String.class));

        var body = captor.getValue().getBody();
        assertNotNull(body);

        var interactive = (Map<String, Object>) body.get("interactive");
        var action      = (Map<String,Object>) interactive.get("action");
        var sections    = (List<Map<String,Object>>) action.get("sections");
        var rows        = (List<Map<String,String>>) sections.getFirst().get("rows");

        assertEquals(7, rows.size());

        LocalDate hoje        = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        assertEquals(hoje.plusDays(1).format(fmt), rows.getFirst().get("id"));
        assertEquals(hoje.plusDays(7).format(fmt), rows.get(6).get("id"));
    }

    @Test
    void deveEnviarListaDeHorarios_Tem11Horas() {
        String data = "11/01/2025";
        adapter.enviarListaHorarios(PHONE, data);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor =
                ArgumentCaptor.forClass((Class) HttpEntity.class);
        verify(mockRestTemplate).postForEntity(eq(URL), captor.capture(), eq(String.class));

        var body = captor.getValue().getBody();
        assertNotNull(body);

        var interactive = (Map<String,Object>) body.get("interactive");
        var action      = (Map<String,Object>) interactive.get("action");
        var sections    = (List<Map<String,Object>>) action.get("sections");
        var rows        = (List<Map<String,String>>) sections.getFirst().get("rows");

        assertEquals(11, rows.size());
        assertEquals("08:00", rows.getFirst().get("id"));
        assertEquals("18:00", rows.getLast().get("id"));
    }

}
