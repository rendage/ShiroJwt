package com.wang.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
@Slf4j
@Component
public class amqpClient {
    @Autowired
    private RabbitTemplate rabbitTemplate;

 //  @PostConstruct
 /*   public void sendMsg(){
        for (int i=0;i<1000000;i++){
            String request = UUID.randomUUID().toString().replaceAll("_", "");
            rabbitTemplate.convertAndSend("eventMsgDirectExchange","bindingqueueandexchange",request);
            log.debug(">>>>>正在发送第{}消息：>>>>>>>>>>{}",i,request);

        }
    }*/
   // @PostConstruct
    public void sendMsgV2(){
        for (int i=0;i<10000;i++){
            long count=100000444;
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
            pushNotificationRequest.setToId(100000444+i);
            pushNotificationRequest.setMsg("Hello Mr.ren");
            String request = UUID.randomUUID().toString().replaceAll("_", "");
            rabbitTemplate.convertAndSend("eventMsgDirectExchange","bindingqueueandexchange",pushNotificationRequest);
            log.info(">>>>>正在发送第{}消息：>>>>>>>>>>{}",i,request);

        }
    }
}
