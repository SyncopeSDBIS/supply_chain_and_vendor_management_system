package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.security.IApprovalAuthorityService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

/**
 * Serviciu dedicat pentru rezolvarea autoritatii de aprobare a comenzilor din contextul de securitate.
 * Centralizeaza maparea dintre rolurile Spring Security (JWT) si nivelele de aprobare din domeniul DDD.
 */
@Service
public class ApprovalAuthorityService implements IApprovalAuthorityService {

    public UUID extrageUtilizatorId(Authentication auth) {
        if (auth == null) {
            return null;
        }
        String name = auth.getName();
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException ignored) {
            return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }
    }

    public NivelAprobare determinaNivelAprobare(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return null;
        }
        NivelAprobare maxNivel = null;
        for (GrantedAuthority ga : authorities) {
            String role = ga.getAuthority();
            if (role == null) continue;
            role = role.trim().toUpperCase();
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_DIRECTOR_GENERAL") || role.equals("ROLE_DIRECTOR")
                    || role.equals("ROLE_CFO") || role.equals("ROLE_CEO")) {
                return NivelAprobare.DIRECTOR_GENERAL;
            }
            if (role.equals("ROLE_MANAGER_ACHIZITII") || role.equals("ROLE_MANAGER_DEPARTAMENT") || role.equals("ROLE_QUALITY_MANAGER")) {
                if (maxNivel == null || maxNivel.getRang() < NivelAprobare.MANAGER_DEPARTAMENT.getRang()) {
                    maxNivel = NivelAprobare.MANAGER_DEPARTAMENT;
                }
            }
            if (role.equals("ROLE_BUYER") || role.equals("ROLE_RESPONSABIL_ACHIZITII") || role.equals("ROLE_REFERENT_APROVIZIONARE")) {
                if (maxNivel == null || maxNivel.getRang() < NivelAprobare.RESPONSABIL_ACHIZITII.getRang()) {
                    maxNivel = NivelAprobare.RESPONSABIL_ACHIZITII;
                }
            }
        }
        return maxNivel;
    }
}

