package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Event published to Spring ApplicationContext when an AMQP message is consumed from RabbitMQ.
 * Enables horizontal scalability and distributed reactive handling across cluster nodes.
 */
public record AmqpMessageReceivedEvent(
    String consumerId,
    UUID eventId,
    Map<String, Object> payload
) implements Serializable {}

