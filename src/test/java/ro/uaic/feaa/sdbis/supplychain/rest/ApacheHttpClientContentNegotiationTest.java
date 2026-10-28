package ro.uaic.feaa.sdbis.supplychain.rest;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Sprint 4 - Apache HttpClient 5 Dual Content-Negotiation Tests (JSON + XML) (ADR-007, ADR-008)")
public class ApacheHttpClientContentNegotiationTest {

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    @DisplayName("Apache HttpClient 5: GET /api/facturi/scadente cu Accept application/json returneaza JSON")
    void testGetFacturiScadenteJson() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getBaseUrl() + "/facturi/scadente");
            request.addHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                assertEquals(200, response.getCode());
                String contentType = response.getHeader("Content-Type").getValue();
                assertTrue(contentType.contains("application/json"), "Content-Type trebuie sa fie application/json, a fost: " + contentType);
                String body = EntityUtils.toString(response.getEntity());
                assertNotNull(body);
            }
        }
    }

    @Test
    @DisplayName("Apache HttpClient 5: Content Negotiation XML prin parametrul ?format=xml")
    void testContentNegotiationXmlParameter() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getBaseUrl() + "/facturi/scadente?format=xml");

            try (CloseableHttpResponse response = client.execute(request)) {
                assertEquals(200, response.getCode());
                String contentType = response.getHeader("Content-Type").getValue();
                assertTrue(contentType.contains("xml"), "Content-Type trebuie sa suporte XML prin ?format=xml, a fost: " + contentType);
            }
        }
    }

    @Test
    @DisplayName("Apache HttpClient 5: POST /api/facturi returneaza 201 Created si JSON payload")
    void testPostFacturaWithHttpClient() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(getBaseUrl() + "/facturi");
            post.addHeader("Accept", "application/json");
            post.setEntity(new UrlEncodedFormEntity(List.of(
                    new BasicNameValuePair("serie", "HC-FACT"),
                    new BasicNameValuePair("numar", "5501"),
                    new BasicNameValuePair("dataEmitere", "2026-04-01"),
                    new BasicNameValuePair("dataScadenta", "2026-04-30"),
                    new BasicNameValuePair("comandaId", UUID.randomUUID().toString()),
                    new BasicNameValuePair("furnizorId", UUID.randomUUID().toString()),
                    new BasicNameValuePair("moneda", "EUR"),
                    new BasicNameValuePair("materialCod", "TABLA-02"),
                    new BasicNameValuePair("cantitate", "25.0"),
                    new BasicNameValuePair("unitate", "BUC"),
                    new BasicNameValuePair("pretUnitar", "120.0")
            )));

            try (CloseableHttpResponse response = client.execute(post)) {
                assertEquals(201, response.getCode());
                String body = EntityUtils.toString(response.getEntity());
                assertTrue(body.contains("facturaId"));
                assertTrue(body.contains("Inregistrata"));
            }
        }
    }
}
