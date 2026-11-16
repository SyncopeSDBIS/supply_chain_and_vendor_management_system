package ro.uaic.feaa.sdbis.supplychain.securitate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = "security.seed-default-users=true")
@DisplayName("DevSecOps - Enterprise JWT Authentication & Access Control Tests")
public class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Auth: Login cu credentiale valide returneaza token JWT semnat")
    void testLoginSuccess() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("admin", "admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Auth: Login cu parola gresita returneaza 401 Unauthorized")
    void testLoginFailure() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("admin", "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Auth: Acces pe endpoint protejat folosind Bearer Token JWT")
    void testAccessWithBearerToken() throws Exception {
        // 1. Login ca contabil
        AuthLoginRequest loginReq = new AuthLoginRequest("contabil", "contabil123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // 2. Acces modul facturi cu Bearer token
        mockMvc.perform(get("/api/facturi/scadente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Auth: Token-ul de Buyer primeste 403 Forbidden pe modulul de facturi")
    void testBuyerTokenForbiddenOnInvoices() throws Exception {
        // 1. Login ca achizitor
        AuthLoginRequest loginReq = new AuthLoginRequest("achizitor", "achizitor123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // 2. Acces modul facturi cu token de achizitor -> 403 Forbidden
        mockMvc.perform(get("/api/facturi/scadente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}

