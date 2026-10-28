package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Sprint 4 - RestTemplate Endpoints Integration Tests (ADR-008, ADR-009)")
public class RestTemplateIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    @DisplayName("RestTemplate: GET /api/facturi/scadente returneaza HTTP 200 OK")
    void testGetFacturiScadenteWithRestTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-User-Role", "ROLE_ACCOUNTANT");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/facturi/scadente",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("RestTemplate: GET /api/facturi/scadente cu parametrul ?format=xml returneaza XML")
    void testGetFacturiScadenteXmlWithRestTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ROLE_ACCOUNTANT");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/facturi/scadente?format=xml",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_XML));
    }

    @Test
    @DisplayName("RestTemplate: GET /api/comenzi/rapoarte/otif returneaza raportul OTIF cu HTTP 200")
    void testGetComenziCollectionWithRestTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ROLE_BUYER");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/comenzi/rapoarte/otif",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
