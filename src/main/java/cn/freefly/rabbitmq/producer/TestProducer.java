package cn.freefly.rabbitmq.producer;

import cn.freefly.rabbitmq.config.RabbitMqProperties;
import cn.freefly.rabbitmq.dto.SendObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Description:
 * 发送消息确认：用来确认生产者 producer 将消息发送到 broker ，broker 上的交换机 exchange 再投递给队列 queue的过程中，消息是否成功投递。
 * 消息从 producer 到 rabbitmq broker有一个 confirmCallback 确认模式。
 * 消息从 exchange 到 queue 投递失败有一个 returnCallback 退回模式。
 * 我们可以利用这两个Callback来确保消的100%送达
 *
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
@Component
public class TestProducer implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback{
    private static final Logger logger = LoggerFactory.getLogger(TestProducer.class);

    @Resource(name="baseRabbitTemplate")
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    public void sendMsgTest(SendObj sendObj){
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        logger.info("TestProducer发送correlationData: " + correlationData.getId());

        /**
         * 确保消息发送失败后可以重新返回到队列中
         * 注意：yml需要配置 publisher-returns: true
         */
        rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.convertAndSend(rabbitMqProperties.getQueue().getExchange(), rabbitMqProperties.getQueue().getRoutingkey(), sendObj
                ,message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
                , correlationData);
    }

    /**
     * 消息只要被 rabbitmq broker 接收到就会触发 confirmCallback 回调
     * @param correlationData 对象内部只有一个 id 属性，用来表示当前消息的唯一性
     * @param ack 消息投递到broker 的状态，true表示成功
     * @param cause 表示投递失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info("confirm: " + correlationData.getId()+" ,ack: "+ack+" ,cause: "+cause);

    }

    /**
     * 如果消息未能投递到目标 queue 里将触发回调 returnCallback ，一旦向 queue 投递消息未成功，这里一般会记录下当前消息的详细投递数据，方便后续做重发或者补偿等操作
     * @param returnedMessage
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        logger.info("returnedMessage ===> "+returnedMessage.getReplyCode()+",replyText="+returnedMessage.getReplyText()+",exchange="+returnedMessage.getExchange()+",routingKey="+returnedMessage.getRoutingKey());

    }
}
