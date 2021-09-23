package cn.freefly.rabbitmq.dto;

/**
 * @Description:
 * @author: xhzl.xiaoyunfei
 * @date: 2021.09.22
 */
public class SendObj {
    private String name;
    private String age;

    public SendObj(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
