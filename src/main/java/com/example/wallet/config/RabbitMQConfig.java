package com.example.wallet.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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

    @Bean
    public TopicExchange transferExchange() {
        return new TopicExchange("transferExchange");
    }

    @Bean
    public Binding transferBinding() {
        return BindingBuilder.bind(transactionQueue()).to(transferExchange()).with("transfer"); // Routing key for main queue
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // To serialize TransferRequest to JSON
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
