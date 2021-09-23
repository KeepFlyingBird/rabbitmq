package cn.freefly.rabbitmq.service;

import cn.freefly.rabbitmq.dto.SendObj;

import java.util.List;

public interface TestService {
    void sendObjList(List<SendObj> list);
}
