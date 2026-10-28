package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Email implements Serializable {
    private String email;

    protected Email() {}

    public Email(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Adresa de email invalida: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public static boolean isValidEmail(String e) {
        return e != null && e.contains("@") && e.contains(".");
    }

    public static Email of(String e) {
        return new Email(e);
    }

    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email1)) return false;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() { return Objects.hash(email); }

    @Override
    public String toString() { return email; }
}
