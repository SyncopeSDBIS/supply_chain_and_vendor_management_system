package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import java.io.Serializable;
import java.util.UUID;

public record InspectorCreatResponse(
        UUID inspectorId
) implements Serializable {}
