package com.wang.util;

import com.google.gson.internal.$Gson$Preconditions;
import com.wang.Application;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Mr.ren
 * @desroiption 流水线
 * @date 2021/5/19 9:26
 */
@Data
class Something {
    private String name;

    public Something(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Something{" +
                "name='" + name + '\'' +
                '}';
    }
}

public class PipeLineUtils implements ApplicationContextAware {

    private static Logger log = LoggerFactory.getLogger(PipeLineUtils.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static <T> T getData(Supplier<T> function) {
        return function.get();
    }

    public static <T> Consumer<T> first(Consumer<T> function) {
        return (T t) -> function.accept(t);
    }

   /* public static <T> Consumer<T> andThen(T data,Consumer<T> function){
         return function.accept(data);
    }*/

    public static <T> void last(T data, Consumer<T> function) {
        function.accept(data);
    }

    public PipeLineUtils.Builder pipeLine() {
        return new Builder();
    }

    public class Builder {
        public <T> T first(T t,Consumer<T> function) {
            function.accept(t);
            return null;
        }

        public <T> T andThen(Consumer<T> function) {
            return null;
        }

        public <T> void last(Consumer<T> function) {

        }

        public <T> T build(T t) {
            T bean = (T) applicationContext.getBean(t.getClass());
            return bean;
        }
    }


    public static void main(String[] args) {
        Something hello = new Something("HELLO");
        Consumer<Something> first = PipeLineUtils.first((p) -> {
            hello.setName("guoshanshan");
        });
        ArrayList<Object> list = new ArrayList<>();
    //    System.out.println("first方法" + first.accept(hello););
/*
        Something data = PipeLineUtils.getData(p -> { return hello; });
        System.out.println(data);*/
    }


}
