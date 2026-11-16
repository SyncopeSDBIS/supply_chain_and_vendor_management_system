package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * Enterprise AMQP RabbitMQ Configuration (ADR-004, AMQP-01, AMQP-AUDIT).
 * Declares domain topic exchanges, queues per bounded context, dead-letter routing (DLX & DLQ),
 * reliable message serialization with JSR-310, and listener container factory with exponential backoff retry.
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "supplychain.events.exchange";
    public static final String EXCHANGE_DLX = "supplychain.events.dlx";

    public static final String QUEUE_COMENZI = "comenzi.events.queue";
    public static final String QUEUE_CALITATE = "calitate.events.queue";
    public static final String QUEUE_FACTURI = "facturi.events.queue";
    public static final String QUEUE_SHARED = "shared.events.queue";
    public static final String QUEUE_DLQ = "supplychain.events.dlq";

    public static final String ROUTING_COMENZI = "comenzi.#";
    public static final String ROUTING_CALITATE = "calitate.#";
    public static final String ROUTING_FACTURI = "facturi.#";
    public static final String ROUTING_SHARED = "shared.#";
    public static final String ROUTING_DLQ = "dlq.#";

    @Bean
    public TopicExchange supplyChainExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_DLX).durable(true).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_DLQ);
    }

    @Bean
    public Queue comenziQueue() {
        return QueueBuilder.durable(QUEUE_COMENZI)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.comenzi")
                .build();
    }

    @Bean
    public Queue calitateQueue() {
        return QueueBuilder.durable(QUEUE_CALITATE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.calitate")
                .build();
    }

    @Bean
    public Queue facturiQueue() {
        return QueueBuilder.durable(QUEUE_FACTURI)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.facturi")
                .build();
    }

    @Bean
    public Queue sharedQueue() {
        return QueueBuilder.durable(QUEUE_SHARED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "dlq.shared")
                .build();
    }

    @Bean
    public Binding comenziBinding(Queue comenziQueue, TopicExchange supplyChainExchange) {
        return BindingBuilder.bind(comenziQueue).to(supplyChainExchange).with(ROUTING_COMENZI);
    }

    @Bean
    public Binding calitateBinding(Queue calitateQueue, TopicExchange supplyChainExchange) {
        return BindingBuilder.bind(calitateQueue).to(supplyChainExchange).with(ROUTING_CALITATE);
    }

    @Bean
    public Binding facturiBinding(Queue facturiQueue, TopicExchange supplyChainExchange) {
        return BindingBuilder.bind(facturiQueue).to(supplyChainExchange).with(ROUTING_FACTURI);
    }

    @Bean
    public Binding sharedBinding(Queue sharedQueue, TopicExchange supplyChainExchange) {
        return BindingBuilder.bind(sharedQueue).to(supplyChainExchange).with(ROUTING_SHARED);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RabbitMqConfig.class);

    @Bean
    public MessageConverter jsonMessageConverter(@Autowired(required = false) ObjectMapper objectMapper) {
        ObjectMapper mapper = (objectMapper != null) ? objectMapper.copy() : new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setPrefetchCount(10);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 5000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
        factory.setAdviceChain(retryInterceptor);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setMandatory(true);

        // AMQP-01: Publisher Confirms - Verificare persistenta mesaj pe disc in broker
        template.setConfirmCallback((correlationData, ack, cause) -> {
            String corrId = (correlationData != null) ? correlationData.getId() : "N/A";
            if (ack) {
                log.info("[RabbitMQ Publisher Confirm ACK] Mesaj persistat pe disc in broker: correlationId={}", corrId);
            } else {
                log.error("[RabbitMQ Publisher Confirm NACK] Respins de broker! correlationId={}, motiv={}", corrId, cause);
            }
        });

        // AMQP-01: Returns Callback - Captare mesaje nerutabile (unroutable messages cu mandatory=true)
        template.setReturnsCallback(returned -> {
            log.error("[RabbitMQ Mandatory Return] Mesaj nerutabil returnat de broker! ReplyCode={}, ReplyText={}, Exchange={}, RoutingKey={}",
                    returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey());
        });

        return template;
    }
}
