package cn.freefly.rabbitmq.controller;

import cn.freefly.rabbitmq.dto.SendObj;
import cn.freefly.rabbitmq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping("/rabbitmq/test")
    public void Test(){
        List<SendObj> list = new ArrayList<SendObj>();
        for (int i=0;i<300;i++){
            list.add(new SendObj("张三"+i,"10"+i));
        }
        testService.sendObjList(list);
    }
}
