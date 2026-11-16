package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.DeadLetterMessageEntity;
import ro.uaic.feaa.sdbis.supplychain.shared.IDeadLetterParkingLotRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.messaging.RabbitMqConfig;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AMQP-01: RabbitMQ Hardening, JSR-310 Converter, DLQ Parking Lot & Atomic Inbox Tests")
public class AmqpHardeningIntegrationTest {

    @Autowired
    private MessageConverter jsonMessageConverter;

    @Autowired
    private IDeadLetterParkingLotRepo deadLetterRepo;

    @Autowired
    private InboxRepo inboxRepo;

    record SampleTimePayload(UUID id, Instant instant, LocalDate date, String desc) {}

    @Test
    @DisplayName("AMQP Converter: Serializes and deserializes JSR-310 Instant and LocalDate without errors")
    void testJsr310SerializationAndDeserialization() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();
        SampleTimePayload payload = new SampleTimePayload(id, now, today, "Test AMQP JSR310");

        Message message = jsonMessageConverter.toMessage(payload, new MessageProperties());
        assertNotNull(message, "AMQP message must not be null");

        Object deserialized = jsonMessageConverter.fromMessage(message);
        assertNotNull(deserialized, "Deserialized payload must not be null");

        if (deserialized instanceof SampleTimePayload s) {
            assertEquals(id, s.id());
            assertEquals(today, s.date());
        } else if (deserialized instanceof Map<?, ?> map) {
            assertEquals(id.toString(), map.get("id").toString());
            assertNotNull(map.get("instant"));
            assertNotNull(map.get("date"));
        } else {
            fail("Unexpected deserialized type: " + deserialized.getClass());
        }
    }

    @Test
    @Transactional
    @DisplayName("AMQP DLQ: Poison pills are persisted into shared_dlq_parking_lot without data loss")
    void testDlqParkingLotPersistence() {
        UUID id = UUID.randomUUID();
        DeadLetterMessageEntity entity = new DeadLetterMessageEntity(
                id,
                "TestDlqConsumer",
                "dlq.comenzi.test",
                "{\"eventId\":\"" + id + "\",\"error\":\"bad payload\"}",
                "Deserialization failed fatally",
                Instant.now()
        );

        deadLetterRepo.salveazaMesajEsuat(entity);

        Optional<DeadLetterMessageEntity> retrieved = deadLetterRepo.gasesteDupaId(id);
        assertTrue(retrieved.isPresent(), "Entity must be present in DLQ parking lot");
        assertEquals("TestDlqConsumer", retrieved.get().getConsumerId());
        assertEquals("dlq.comenzi.test", retrieved.get().getRoutingKey());
        assertFalse(retrieved.get().isRetried());

        retrieved.get().markRetried();
        assertTrue(retrieved.get().isRetried());
        assertNotNull(retrieved.get().getRetriedAt());
    }

    @Test
    @DisplayName("AMQP Inbox: Atomic registration eliminates TOCTOU race conditions under simulated concurrency")
    void testAtomicInboxRegistrationUnderConcurrency() throws Exception {
        UUID eventId = UUID.randomUUID();
        String consumer = "ConcurrentAmqpConsumer";
        int threads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Boolean>> tasks = IntStream.range(0, threads)
                .mapToObj(i -> (Callable<Boolean>) () -> inboxRepo.incearcaInregistrare(consumer, eventId))
                .collect(Collectors.toList());

        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        int successCount = 0;
        for (Future<Boolean> f : futures) {
            if (f.get()) {
                successCount++;
            }
        }

        assertEquals(1, successCount, "Exactly ONE concurrent thread must succeed in claiming the event");
        assertTrue(inboxRepo.existaEveniment(consumer, eventId), "Inbox must record event as existing");
    }

    @Test
    @DisplayName("AMQP Topology: Shared queue and routing key constants are properly declared")
    void testTopologyConstants() {
        assertEquals("shared.events.queue", RabbitMqConfig.QUEUE_SHARED);
        assertEquals("shared.#", RabbitMqConfig.ROUTING_SHARED);
    }
}
