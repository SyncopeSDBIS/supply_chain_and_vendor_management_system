package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Request DTO for creating a quality inspector.
 */
public class CreeazaInspectorRequest implements Serializable {

    @NotBlank(message = "Prenumele inspectorului este obligatoriu")
    @Size(min = 2, max = 50, message = "Prenumele trebuie sa contina intre 2 si 50 caractere")
    private String firstName;

    @NotBlank(message = "Numele inspectorului este obligatoriu")
    @Size(min = 2, max = 50, message = "Numele trebuie sa contina intre 2 si 50 caractere")
    private String lastName;

    public CreeazaInspectorRequest() {}

    public CreeazaInspectorRequest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}

