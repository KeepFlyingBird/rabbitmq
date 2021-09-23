package cn.freefly.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassNmae RabbitConfig 公共调用方法
 * @Author xiao.yunfei
 * @Date 2021/09/22 10:20
 * @Desc
 */
@Configuration
public class RabbitMqConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Bean("baseRabbitConnFactory")
    public ConnectionFactory baseRabbitConnFactory(){
        CachingConnectionFactory connFactory = new CachingConnectionFactory();
        connFactory.setAddresses(rabbitMqProperties.getHost());
        connFactory.setPort(rabbitMqProperties.getPort());
        connFactory.setUsername(rabbitMqProperties.getUsername());
        connFactory.setPassword(rabbitMqProperties.getPassword());
        connFactory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        connFactory.setPublisherConfirmType(rabbitMqProperties.getPublisherConfirmType());
        return connFactory;
    }

    @Bean("baseRabbitAdmin")
    public RabbitAdmin baseRabbitAdmin(
            @Qualifier("baseRabbitConnFactory") ConnectionFactory connFactory
    ){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connFactory);
        //autoStartup 必须要设为 true ，否则Spring容器不会加载RabbitAdmin类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean("baseRabbitTemplate")
    public RabbitTemplate baseRabbitTemplate(
            @Qualifier("baseRabbitConnFactory") ConnectionFactory connFactory
    ){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connFactory);
        // 设置消息转换Json格式传输
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
      return rabbitTemplate;
    }

    @Bean("baseRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory baseRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("baseRabbitConnFactory") ConnectionFactory connFactory
    ){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 设置并发消费者的个数
        factory.setConcurrentConsumers(3);
        // 设置最大消费者数量
//        factory.setMaxConcurrentConsumers(5);
        // 设置消费者每次从队列中读取消息个数
        factory.setPrefetchCount(5);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        factory.setTaskExecutor(taskExecutor);
        configurer.configure(factory, connFactory);

        return factory;
    }

    /**
     * 绑定队列和交换机，每个队列都要进行一次绑定
     * @param rabbitAdmin
     * @return
     */
    @Bean("bindingQueue")
    Binding bindingQueue(@Qualifier("baseRabbitAdmin") RabbitAdmin rabbitAdmin){
        //声明交换机，并持久化
        TopicExchange topicExchange = new TopicExchange(rabbitMqProperties.getQueue().getExchange(),true,false);
        rabbitAdmin.declareExchange(topicExchange);
        //声明队列，并持久化
        Queue queue = new Queue(rabbitMqProperties.getQueue().getQueue(),true,false,false);
        rabbitAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(rabbitMqProperties.getQueue().getRoutingkey());
        rabbitAdmin.declareBinding(binding);
        logger.debug("testRabbitMq:队列与主题型交换机绑定完成");
        return binding;
    }
}
