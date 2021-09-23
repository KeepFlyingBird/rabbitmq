package cn.freefly.rabbitmq.service.impl;

import cn.freefly.rabbitmq.dto.SendObj;
import cn.freefly.rabbitmq.producer.TestProducer;
import cn.freefly.rabbitmq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestProducer testProducer;

    @Override
    public void sendObjList(List<SendObj> list) {
        list.forEach((SendObj sendObj) -> {
            testProducer.sendMsgTest(sendObj);
        });
    }
}
