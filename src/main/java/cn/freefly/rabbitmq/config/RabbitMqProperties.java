package cn.freefly.rabbitmq.config;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description:
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;
    private CachingConnectionFactory.ConfirmType publisherConfirmType;
    private TestQueue queue;
    private TestQueue queue2;

    @Data
    public static class TestQueue {
        private String queue;
        private String exchange;
        private String routingkey;
    }
}
