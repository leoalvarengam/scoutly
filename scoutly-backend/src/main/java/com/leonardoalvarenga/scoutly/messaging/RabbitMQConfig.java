package com.leonardoalvarenga.scoutly.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SCRAPING_QUEUE = "scoutly.scraping.queue";

    @Bean
    public Queue scrapingQueue() {
        return new Queue(SCRAPING_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}