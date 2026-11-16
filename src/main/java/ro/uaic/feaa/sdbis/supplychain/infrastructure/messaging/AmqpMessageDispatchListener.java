package ro.uaic.feaa.sdbis.supplychain.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaNeconfirmataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.InfrastructureService;
import ro.uaic.feaa.sdbis.supplychain.shared.messaging.AmqpMessageReceivedEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Listener decuplat de infrastructura ce consuma evenimentele locale AmqpMessageReceivedEvent
 * propagate de RabbitMqEventConsumerService si le ruteaza ca evenimente publice de domeniu.
 */
@Component
@InfrastructureService
public class AmqpMessageDispatchListener {

    private static final Logger log = LoggerFactory.getLogger(AmqpMessageDispatchListener.class);

    private final ApplicationEventPublisher eventPublisher;
    private final AtomicLong mesajeProcesate = new AtomicLong(0);

    public AmqpMessageDispatchListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onAmqpMessageReceived(AmqpMessageReceivedEvent event) {
        log.info("[AMQP Dispatch Listener] Primit mesaj din coada de la consumer '{}' (eventId: {})",
                event.consumerId(), event.eventId());

        mesajeProcesate.incrementAndGet();
        Map<String, Object> payload = event.payload();

        if (payload != null && payload.containsKey("eventType")) {
            String eventType = payload.get("eventType").toString();
            log.info("[AMQP Dispatch Listener] Procesare tip eveniment distribuit: {}", eventType);

            if ("ComandaNeconfirmataEvent".equalsIgnoreCase(eventType) && payload.containsKey("comandaId")) {
                try {
                    UUID comandaId = UUID.fromString(payload.get("comandaId").toString());
                    eventPublisher.publishEvent(new ComandaNeconfirmataPublicEvent(
                            UUID.randomUUID(),
                            Instant.now(),
                            IdComanda.of(comandaId)
                    ));
                } catch (Exception ex) {
                    log.warn("[AMQP Dispatch Listener] Nu s-a putut parsa comandaId: {}", ex.getMessage());
                }
            }
        }
    }

    public long getNumarMesajeProcesate() {
        return mesajeProcesate.get();
    }
}
