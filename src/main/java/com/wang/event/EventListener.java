package com.wang.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;


@Component
public class EventListener {

    private static Logger log = LoggerFactory.getLogger(EventListener.class);

    public static <T extends ApplicationEvent> void motify(T applicationEvent, List<ApplicationEvent> events) {
        Assert.notNull(applicationEvent, "通知事件不能为NULL!");
        Assert.notNull(events, "通知事件不能为NULL!");
        if (events.contains(applicationEvent)) {
            for (ApplicationEvent event: events) {
                log.info("事件{}已经被监听", event.getClass().getName());
            }
        }
        events.clear();
    }

}
