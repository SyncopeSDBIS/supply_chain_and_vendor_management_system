package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.StoredDomainEvent;

/**
 * Enterprise AMQP Event Publisher for Transactional Outbox integration (ADR-004, AMQP-01).
 * Routes domain events to the RabbitMQ topic exchange based on event type prefix.
 * Emits CorrelationData for reliable publisher confirmations.
 * Re-throws broker exceptions when enabled to prevent premature Outbox event acknowledgment.
 */
@Component
public class AmqpEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${supplychain.rabbitmq.enabled:false}")
    private boolean rabbitMqEnabled;

    public AmqpEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEvent(StoredDomainEvent event) {
        publishEvent(event, new CorrelationData(event.eventId().toString()));
    }

    public void publishEvent(StoredDomainEvent event, CorrelationData correlationData) {
        if (!rabbitMqEnabled) {
            log.debug("[AMQP Outbox Dispatch] RabbitMQ publishing disabled. Skipping event {}", event.eventId());
            return;
        }
        String routingKey = determineRoutingKey(event.eventType());
        try {
            if (correlationData != null) {
                rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, (Object) event, correlationData);
            } else {
                rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, (Object) event);
            }
            log.info("[AMQP Outbox Dispatch] Published event {} to exchange {} with key {}",
                    event.eventId(), RabbitMqConfig.EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            log.error("[AMQP Broker Failure] Failed to dispatch outbox event {} to RabbitMQ: {}",
                    event.eventId(), e.getMessage());
            throw new RuntimeException("Eroare la publicarea in brokerul RabbitMQ pentru evenimentul: " + event.eventId(), e);
        }
    }

    public void publishDomainEvent(DomainEvent event) {
        publishDomainEvent(event, new CorrelationData(event.eventId().toString()));
    }

    public void publishDomainEvent(DomainEvent event, CorrelationData correlationData) {
        if (!rabbitMqEnabled) {
            log.debug("[AMQP Direct Dispatch] RabbitMQ publishing disabled. Skipping domain event {}", event.eventId());
            return;
        }
        String routingKey = determineRoutingKey(event.getClass().getSimpleName());
        try {
            if (correlationData != null) {
                rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, (Object) event, correlationData);
            } else {
                rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, (Object) event);
            }
            log.info("[AMQP Direct Dispatch] Published domain event {} with key {}",
                    event.eventId(), routingKey);
        } catch (Exception e) {
            log.error("[AMQP Broker Failure] Failed to dispatch domain event {} to RabbitMQ: {}",
                    event.eventId(), e.getMessage());
            throw new RuntimeException("Eroare la publicarea in brokerul RabbitMQ pentru evenimentul de domeniu: " + event.eventId(), e);
        }
    }

    private String determineRoutingKey(String eventType) {
        String lower = eventType.toLowerCase();
        if (lower.contains("comanda") || lower.contains("order")) {
            return "comenzi." + lower;
        } else if (lower.contains("furnizor") || lower.contains("inspectie") || lower.contains("ncr") || lower.contains("supplier") || lower.contains("calitate")) {
            return "calitate." + lower;
        } else if (lower.contains("factura") || lower.contains("plata") || lower.contains("nir") || lower.contains("invoice")) {
            return "facturi." + lower;
        }
        return "shared." + lower;
    }
}
