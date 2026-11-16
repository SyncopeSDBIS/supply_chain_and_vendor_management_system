package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Messaging - AmqpEventPublisher Resilience Tests")
public class AmqpEventPublisherTest {

    private final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private final AmqpEventPublisher publisher = new AmqpEventPublisher(rabbitTemplate);

    record TestDomainEvent(UUID eventId, Instant occurredOn, String info) implements DomainEvent {}

    @Test
    @DisplayName("AMQP Resilience: Re-throws exception on broker failure to prevent premature Outbox acknowledgment")
    void testRethrowsExceptionWhenBrokerFails() {
        ReflectionTestUtils.setField(publisher, "rabbitMqEnabled", true);
        doThrow(new AmqpException("Broker connection refused"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        doThrow(new AmqpException("Broker connection refused"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class), any(org.springframework.amqp.rabbit.connection.CorrelationData.class));

        TestDomainEvent event = new TestDomainEvent(UUID.randomUUID(), Instant.now(), "Order1");

        assertThrows(RuntimeException.class, () -> publisher.publishDomainEvent(event));
    }

    @Test
    @DisplayName("AMQP Resilience: Skips broker call when rabbitMqEnabled is false without throwing")
    void testSkipsDispatchWhenDisabled() {
        ReflectionTestUtils.setField(publisher, "rabbitMqEnabled", false);

        TestDomainEvent event = new TestDomainEvent(UUID.randomUUID(), Instant.now(), "Order2");

        assertDoesNotThrow(() -> publisher.publishDomainEvent(event));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class), any(org.springframework.amqp.rabbit.connection.CorrelationData.class));
    }
}

