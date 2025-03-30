package com.example.wallet.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMQConfig {
    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable("transactionQueue")
                .withArgument("x-dead-letter-exchange", "dlx")
                .withArgument("x-dead-letter-routing-key", "transactionQueue.dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("transactionQueue.dlq", true);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("transactionQueue.dlq");
    }
}
