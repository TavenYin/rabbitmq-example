package com.github.taven;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeMessage(String message) {
        System.out.println("consume message:" + message);
    }

}