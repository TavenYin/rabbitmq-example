package com.github.taven.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 写点东西哈
 * 可以利用死信的特点来做延时队列，例如：订单10s后未支付则删除，将X队列的消息过期时间设置为10s，X队列没有消费者，当消息过期后
 * 该消息会被转到死信队列中，此时死信队列可以处理相应的逻辑
 *
 */
@Configuration
public class RabbitmqConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";

    @Bean
    public Queue queue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        // 可不设置，如果设置路由到死信时，会以该RoutingKey作为路由键，如果不设置保持原先的RoutingKey
        args.put("x-dead-letter-routing-key", "some-routing-key");
        // 消息过期时间，可以注释掉user队列的消费者，超过时间后，会进入死信队列
        args.put("x-message-ttl", 10000);
        return new Queue("user", true, false, false, args);
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange("user_exchange");
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(queue()).to(userExchange()).with("user_routingKey");
    }

    // 三种情况，消息会转发到死信
    // 1. 消息被拒绝（Basic.Reject或Basic.Nack）并且设置 requeue 参数的值为 false
    // 2. 消息过期了
    // 3. 队列达到最大的长度
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead_letter_queue");
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Configuration
    public class RabbitTemplateWrapper {
        public RabbitTemplateWrapper(RabbitTemplate rabbitTemplate) {
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                // 是否成功发送到Exchange
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
