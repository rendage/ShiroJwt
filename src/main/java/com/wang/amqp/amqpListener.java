package com.wang.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class amqpListener {
    private static Logger log = LoggerFactory.getLogger(amqpListener.class);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 6, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    private static int CAPACITY = 10000;
    private String QUEUE_NAME_EVENT_MSG = "eventMsgQueue";

    /**
     * amqp 批次拉取 pull
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(queues = {"eventMsgQueue"})
    public void getMessage(Message message, Channel channel) throws Exception {
        log.error(">>>>>>>>>>开始第次数{}消费消息message>>>>>>>>>>>");

        long timeMillis = System.currentTimeMillis();
        List<GetResponse> list = Collections.synchronizedList(new LinkedList<>());
        int poolSize = threadPoolExecutor.getCorePoolSize();
        CountDownLatch countDownLatch = new CountDownLatch(poolSize);
        Lock lock = new ReentrantLock();
        int count = channel.queueDeclarePassive("eventMsgQueue").getMessageCount();
        long tag = 0;
        for (int i = 0; i < count; i++) {
            GetResponse getResponse = channel.basicGet("eventMsgQueue", false);
            tag = getResponse.getEnvelope().getDeliveryTag();
            list.add(getResponse);
            if (count > CAPACITY && list.size() % CAPACITY == 0) {
                sendMsg(message, list, countDownLatch, lock);
                continue;
            }
            if (count < CAPACITY && list.size() % (CAPACITY / 100) == 0) {
                sendMsg(message, list, countDownLatch, lock);
                continue;
            }
            if (count < CAPACITY / 100) {
                sendMsg(message, list, countDownLatch, lock);
                continue;
            }

        }
        if (list.size() > 0) {
            sendMsg(message, list, countDownLatch, lock);
        }
        threadPoolExecutor.shutdown();
        channel.basicAck(tag, true);
        channel.close();
        long t = System.currentTimeMillis();
        log.error(">>>>>>>>>>结束消费消息message>>>>，用时:{}", t - timeMillis);
    }

    private void sendMsg(Message message, List<GetResponse> list, CountDownLatch countDownLatch, Lock lock) throws Exception {
        try {
            list.stream().forEach(p -> {
                threadPoolExecutor.execute(() -> {
                    lock.lock();
                    log.info(">>>>>>>>>>>>>>>>>>消费第{}消息{},\r\n >>>>>>>>>>>>successful!",p.getEnvelope().getDeliveryTag(), p.getBody());
                    countDownLatch.countDown();
                    lock.unlock();
                });
            });
            countDownLatch.await();
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>>消息{}:消费异常{}", message.getBody(), e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            list.clear();
        }
    }



}
