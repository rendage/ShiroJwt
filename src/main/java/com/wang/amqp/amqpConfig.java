package com.wang.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class amqpConfig {

    @Bean
    public Queue eventMsgQueue(){
        return new Queue("eventMsgQueue");
    }
    @Bean
    public DirectExchange eventMsgDirectExchange(){
        return new DirectExchange("eventMsgDirectExchange");
    }
    @Bean
    public Binding queueBindingExchange(Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with("bindingqueueandexchange");
    }
  /*  @Bean
    public Queue eventMsgQueueV2(){
        return new Queue("eventMsgQueueV2");
    }
    @Bean
    public DirectExchange eventMsgDirectExchangeV2(){
        return new DirectExchange("eventMsgDirectExchangeV2");
    }
    @Bean
    public Binding queueBindingExchangeV2(Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with("bindingqueueandexchangeV2");
    }*/


}
