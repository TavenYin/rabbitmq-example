package com.github.taven;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class RabbitMQConfig {

    public final static String QUEUE_NAME = "spring-boot-queue";
    public final static String EXCHANGE_NAME = "spring-boot-exchange";
    public final static String ROUTING_KEY = "spring-boot-key";

    // 创建队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    // 创建一个 topic 类型的交换器
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        // connect HAProxy
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("192.168.32.130", 5670);
        connectionFactory.setShuffleAddresses(true);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setExecutor(RabbitThreadPoolExecutor.newFixedThreadPool(1));
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.setConnectionCacheSize(2);
        connectionFactory.setConnectionLimit(16);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    static class RabbitThreadPoolExecutor extends ThreadPoolExecutor {
        public RabbitThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public RabbitThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public RabbitThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public RabbitThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        public void execute(Runnable command) {
            super.execute(command);
        }

        public static ExecutorService newFixedThreadPool(int nThreads) {
            return new RabbitThreadPoolExecutor(nThreads, nThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

    }
}
