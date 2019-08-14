package com.github.taven.receiver;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "user")
public class Receiver {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void receive(String messageStr, Channel channel, Message message) {
        log.info("receive message: {}", messageStr);
        try {
            if ("NACK".equals(messageStr)) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

            } else {
                //通知服务器此消息已经被消费，可从队列删掉， 这样以后就不会重发，否则后续还会在发
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
