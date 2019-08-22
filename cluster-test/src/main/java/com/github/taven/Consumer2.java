package com.github.taven;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer2 {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME + 2)
    public void consumeMessage(String message) {
        System.out.println("consume2 message:" + message);
    }

}