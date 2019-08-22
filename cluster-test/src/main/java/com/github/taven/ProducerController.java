//package com.github.taven;
//
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//
//@RestController
//public class ProducerController {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @GetMapping("/sendMessage")
//    public String sendMessage() {
//        new Thread(() -> {
//            for (int i = 0; i < 100; i++) {
//                LocalDateTime time = LocalDateTime.now();
//                System.out.println("send message: " + time.toString());
//                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, time.toString());
//            }
//        }).start();
//        return "ok";
//    }
//
//}