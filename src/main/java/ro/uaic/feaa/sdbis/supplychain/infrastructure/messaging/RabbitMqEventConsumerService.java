package ro.uaic.feaa.sdbis.supplychain.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ro.uaic.feaa.sdbis.supplychain.shared.DeadLetterMessageEntity;
import ro.uaic.feaa.sdbis.supplychain.shared.IDeadLetterParkingLotRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.InfrastructureService;
import ro.uaic.feaa.sdbis.supplychain.shared.messaging.AmqpMessageReceivedEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.messaging.RabbitMqConfig;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Enterprise RabbitMQ AMQP Consumer (ADR-004, AMQP-01, AMQP-AUDIT).
 * Consuma mesajele distribuite din cozi, asigura procesarea atomica idempotenta prin Inbox,
 * propaga evenimentul in ApplicationEventPublisher si arhiveaza esecurile in DLQ Parking Lot.
 */
@Service
@InfrastructureService
public class RabbitMqEventConsumerService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqEventConsumerService.class);
    private final InboxRepo inboxRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final IDeadLetterParkingLotRepo deadLetterRepo;
    private final ObjectMapper objectMapper;

    public RabbitMqEventConsumerService(InboxRepo inboxRepo, ApplicationEventPublisher eventPublisher) {
        this(inboxRepo, eventPublisher, null, null);
    }

    @Autowired
    public RabbitMqEventConsumerService(InboxRepo inboxRepo,
                                       ApplicationEventPublisher eventPublisher,
                                       @Autowired(required = false) IDeadLetterParkingLotRepo deadLetterRepo,
                                       @Autowired(required = false) ObjectMapper objectMapper) {
        this.inboxRepo = inboxRepo;
        this.eventPublisher = eventPublisher;
        this.deadLetterRepo = deadLetterRepo;
        this.objectMapper = (objectMapper != null) ? objectMapper : new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_COMENZI, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void consumeComenziEvent(Map<String, Object> message) {
        processMessage("ComenziQueueConsumer", message);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_CALITATE, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void consumeCalitateEvent(Map<String, Object> message) {
        processMessage("CalitateQueueConsumer", message);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_FACTURI, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void consumeFacturiEvent(Map<String, Object> message) {
        processMessage("FacturiQueueConsumer", message);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_SHARED, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void consumeSharedEvent(Map<String, Object> message) {
        processMessage("SharedQueueConsumer", message);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_DLQ, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void consumeDeadLetterEvent(Map<String, Object> message,
                                       @Header(name = AmqpHeaders.RECEIVED_ROUTING_KEY, required = false) String routingKey) {
        log.warn("[AMQP Dead-Letter Queue] Unprocessable event routed to DLQ: {}", message);
        if (deadLetterRepo != null && message != null) {
            try {
                String payload = objectMapper.writeValueAsString(message);
                String rKey = (routingKey != null) ? routingKey : "dlq.unknown";
                DeadLetterMessageEntity entity = new DeadLetterMessageEntity(
                        UUID.randomUUID(),
                        "DeadLetterQueueConsumer",
                        rKey,
                        payload,
                        "Event routed to DLQ after exhausting retry attempts",
                        Instant.now()
                );
                deadLetterRepo.salveazaMesajEsuat(entity);
                log.info("[AMQP DLQ Parking Lot] Successfully archived poison pill into shared_dlq_parking_lot (id: {})", entity.getId());
            } catch (Exception ex) {
                log.error("[AMQP DLQ Parking Lot Error] Failed to persist DLQ event: {}", ex.getMessage(), ex);
            }
        }
    }

    private void processMessage(String consumerId, Map<String, Object> message) {
        try {
            if (message != null && message.containsKey("eventId")) {
                UUID eventId = UUID.fromString(message.get("eventId").toString());
                if (!inboxRepo.incearcaInregistrare(consumerId, eventId)) {
                    log.info("[AMQP Consumer {}] Duplicate or already claimed event {} ignored (Atomic Idempotent Inbox).",
                            consumerId, eventId);
                    return;
                }
                eventPublisher.publishEvent(new AmqpMessageReceivedEvent(consumerId, eventId, message));
                log.info("[AMQP Consumer {}] Successfully processed and propagated event {} from broker.", consumerId, eventId);
            }
        } catch (Exception ex) {
            log.error("[AMQP Consumer {}] Error handling broker event: {}", consumerId, ex.getMessage(), ex);
            if (ex instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException("Eroare la procesarea mesajului AMQP: " + ex.getMessage(), ex);
        }
    }
}

