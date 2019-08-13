package com.github.taven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Queue queue() {
        return new Queue("user");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("user_exchange");
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with("user_routingKey6666");
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Configuration
    public class RabbitTemplateWrapper {
        public RabbitTemplateWrapper(RabbitTemplate rabbitTemplate) {
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (ack) {
                    log.info("publish success");
                } else {
                    log.error("publish failed, cause: " + cause);
                }
            });

            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
                // 目前只发现错误的情况会走这个回调，
                // 例如故意将routingKey写错 会报replyCode = 302的错误，NO_ROUTE，不能被转发到任何队列
                log.info("replyCode:{}, replyText:{}, message:{}", replyCode, replyText, message);
            });
        }
    }

}
