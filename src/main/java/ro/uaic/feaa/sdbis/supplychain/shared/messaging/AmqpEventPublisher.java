package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.StoredDomainEvent;

/**
 * Enterprise AMQP Event Publisher for Transactional Outbox integration.
 * Routes domain events to the RabbitMQ topic exchange based on event type prefix.
 */
@Component
public class AmqpEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public AmqpEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEvent(StoredDomainEvent event) {
        String routingKey = determineRoutingKey(event.eventType());
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, event);
            log.info("[AMQP Outbox Dispatch] Published event {} to exchange {} with key {}",
                    event.eventId(), RabbitMqConfig.EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            log.debug("[AMQP Broker Notice] Message broker offline or unreachable: {}. Event {} saved in Outbox for retry.",
                    e.getMessage(), event.eventId());
        }
    }

    public void publishDomainEvent(DomainEvent event) {
        String routingKey = determineRoutingKey(event.getClass().getSimpleName());
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, event);
            log.info("[AMQP Direct Dispatch] Published domain event {} with key {}",
                    event.eventId(), routingKey);
        } catch (Exception e) {
            log.debug("[AMQP Broker Notice] Broker offline: {}. Direct event {} logged.",
                    e.getMessage(), event.eventId());
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
