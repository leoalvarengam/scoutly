package com.leonardoalvarenga.scoutly.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SCRAPING_QUEUE = "scoutly.scraping.queue";
    public static final String ALERTS_QUEUE = "price.alerts.queue";
    public static final String ALERTS_EXCHANGE = "scoutly.alerts.exchange";
    public static final String ALERTS_ROUTING_KEY = "price.alerts.routing.key";

    @Bean
    public Queue scrapingQueue() {
        return new Queue(SCRAPING_QUEUE, true);
    }

    @Bean
    public Queue alertsQueue(){
        return new Queue(ALERTS_QUEUE, true);
    }

    @Bean
    public DirectExchange alertsExchange(){
        return new DirectExchange(ALERTS_EXCHANGE);
    }

    @Bean
    public Binding alertsBinding(Queue alertsQueue, DirectExchange alertsExchange) {
        return BindingBuilder.bind(alertsQueue).to(alertsExchange).with(ALERTS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}