package com.luiz.cadastroclientes;

import com.luiz.cadastroclientes.dto.response.CompraResponseDTO;
import com.luiz.cadastroclientes.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(restTemplate);
        ReflectionTestUtils.setField(notificacaoService, "webhookUrl", "https://n8n.exemplo.com/webhook");
        ReflectionTestUtils.setField(notificacaoService, "webhookSecret", "segredo-super-secreto");
        ReflectionTestUtils.setField(notificacaoService, "adminEmail", "admin@email.com");
    }

    @Test
    @DisplayName("notificarCompra deve enviar o payload correto para a URL configurada com o header de secret")
    void notificarCompraDeveEnviarPayloadCorreto() {
        CompraResponseDTO compraDTO = new CompraResponseDTO(1L, null, 100.0, null, null);

        notificacaoService.notificarCompra(compraDTO);

        ArgumentCaptor<HttpEntity<NotificacaoService.NotificacaoCompraPayload>> requestCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).postForEntity(
                eq("https://n8n.exemplo.com/webhook"),
                requestCaptor.capture(),
                eq(String.class));

        HttpEntity<NotificacaoService.NotificacaoCompraPayload> requestEnviado = requestCaptor.getValue();
        HttpHeaders headers = requestEnviado.getHeaders();

        assertThat(headers.getFirst("X-Webhook-secret")).isEqualTo("segredo-super-secreto");
        assertThat(requestEnviado.getBody()).isNotNull();
        assertThat(requestEnviado.getBody().compra()).isEqualTo(compraDTO);
        assertThat(requestEnviado.getBody().adminEmail()).isEqualTo("admin@email.com");
    }
}
