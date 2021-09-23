package cn.freefly.rabbitmq.consumer;

import cn.freefly.rabbitmq.config.RabbitMqProperties;
import cn.freefly.rabbitmq.dto.SendObj;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
@Component
public class TestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @RabbitListener(queues= {"${spring.rabbitmq.queue.queue}"},containerFactory = "baseRabbitListenerContainerFactory")
    private void receiveMsgTest(final Message message, Channel channel) throws IOException {
        try {
            String msg = new String(message.getBody(), "UTF-8");
            SendObj sendObj = JSONObject.parseObject(msg, SendObj.class);
            Thread.sleep(1000);
            logger.info(Thread.currentThread().getName()+"testConsumer接收数据：name="+sendObj.getName()+", age="+sendObj.getAge());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if (message.getMessageProperties().getRedelivered()) {
                logger.error("消息已重复处理失败,拒绝再次接收...");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
            } else {
                logger.error("消息即将再次返回队列处理...");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}
