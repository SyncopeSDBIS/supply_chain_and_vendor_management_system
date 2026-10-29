package ro.uaic.feaa.sdbis.supplychain.shared.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enterprise AMQP RabbitMQ Configuration (ADR-004).
 * Declares domain topic exchanges, queues per bounded context, dead-letter routing (DLX & DLQ),
 * and reliable message serialization.
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "supplychain.events.exchange";
    public static final String EXCHANGE_DLX = "supplychain.events.dlx";

    public static final String QUEUE_COMENZI = "comenzi.events.queue";
    public static final String QUEUE_CALITATE = "calitate.events.queue";
    public static final String QUEUE_FACTURI = "facturi.events.queue";
    public static final String QUEUE_DLQ = "supplychain.events.dlq";

    public static final String ROUTING_COMENZI = "comenzi.#";
    public static final String ROUTING_CALITATE = "calitate.#";
    public static final String ROUTING_FACTURI = "facturi.#";
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
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
