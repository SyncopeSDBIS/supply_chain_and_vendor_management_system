package ro.uaic.feaa.sdbis.supplychain.comenzi.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;

import java.util.Collection;
import java.util.UUID;

/**
 * Port de aplicatie pentru rezolvarea autoritatii de aprobare a comenzilor (FIX ARCH-02).
 */
public interface IApprovalAuthorityService {
    UUID extrageUtilizatorId(Authentication auth);
    NivelAprobare determinaNivelAprobare(Collection<? extends GrantedAuthority> authorities);
}
