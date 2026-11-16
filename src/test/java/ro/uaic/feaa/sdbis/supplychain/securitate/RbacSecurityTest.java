package ro.uaic.feaa.sdbis.supplychain.securitate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = {
    "security.mock-headers.enabled=true",
    "security.allow-anonymous-fallback=false"
})
@DisplayName("DevSecOps - Zero-Trust Role Based Access Control (RBAC) Tests")
public class RbacSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("RBAC: Buyer accesand modulul de facturi primeste 403 Forbidden")
    void testBuyerCannotAccessInvoices() throws Exception {
        mockMvc.perform(get("/api/facturi/scadente")
                .header("X-User-Role", "ROLE_BUYER")
                .header("X-User-Id", "buyer-user"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC: Quality Inspector accesand comenzi primeste 403 Forbidden")
    void testQualityInspectorCannotAccessOrders() throws Exception {
        mockMvc.perform(get("/api/comenzi/performanta?furnizorId=00000000-0000-0000-0000-000000000001")
                .header("X-User-Role", "ROLE_QUALITY_INSPECTOR")
                .header("X-User-Id", "inspector-user"))
            .andExpect(status().isForbidden());
    }

    @Autowired
    private RbacSecurityFilter rbacSecurityFilter;

    @Test
    @DisplayName("RBAC: Accountant accesand modulul facturi este autorizat")
    void testAccountantCanAccessInvoices() throws Exception {
        mockMvc.perform(get("/api/facturi/scadente")
                .header("X-User-Role", "ROLE_ACCOUNTANT")
                .header("X-User-Id", "accountant-user"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RBAC: Admin accesand modulul facturi este autorizat")
    void testAdminCanAccessInvoices() throws Exception {
        mockMvc.perform(get("/api/facturi/scadente")
                .header("X-User-Role", "ROLE_ADMIN")
                .header("X-User-Id", "admin-user"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RBAC Hardened: Mock headers sunt respinse cand mockHeadersEnabled este false")
    void testMockHeadersRejectedWhenDisabled() throws Exception {
        try {
            rbacSecurityFilter.setMockHeadersEnabled(false);
            rbacSecurityFilter.setAllowAnonymousFallback(false);

            mockMvc.perform(get("/api/facturi/scadente")
                    .header("X-User-Role", "ROLE_ADMIN")
                    .header("X-User-Id", "admin-user"))
                .andExpect(status().isUnauthorized());
        } finally {
            rbacSecurityFilter.setMockHeadersEnabled(true);
            rbacSecurityFilter.setAllowAnonymousFallback(true);
        }
    }
}
