package com.wang.event;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mr.ren
 * @desrciption 事件执行 基于观察者模式
 * @date 2021/5/14 18:39
 */
@Component
public class EventClient {
    private static Logger log = LoggerFactory.getLogger(EventClient.class);

    private static List<ApplicationEvent> events = Lists.newArrayList();

    public  <T extends ApplicationEvent> void addEvent(T applicationEvent) {
        events.add(applicationEvent);
    }
    @Async
    public <T extends ApplicationEvent> void publishEvent(T applicationEvent) {
        log.info("事件{}已经发布", applicationEvent.getClass().getName());
        EventListener.motify(applicationEvent,events);
    }
}

