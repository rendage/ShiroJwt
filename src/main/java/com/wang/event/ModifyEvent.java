package com.wang.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @desrciption 事件监听实体
 * @author renxz
 * @date 2021/5/14 18:29
 */
@Setter
@Getter
public class ModifyEvent extends ApplicationEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    private String msg;

    public ModifyEvent(Object source,String msg) {
        super(source);
        this.msg=msg;
    }


}
