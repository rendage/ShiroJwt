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
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 6, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    private static int CAPACITY = 10000;
    private String QUEUE_NAME_EVENT_MSG="eventMsgQueue";

    /**
     * amqp 批次拉取 pull
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(queues = {"eventMsgQueue"})
    public void getMessage(Message message, Channel channel) throws Exception {
        int total =0;
        log.error(">>>>>>>>>>开始第次数{}消费消息message>>>>>>>>>>>",total++);
        long timeMillis = System.currentTimeMillis();
        List<GetResponse> list = Collections.synchronizedList(new LinkedList<>());
        int poolSize = threadPoolExecutor.getPoolSize();
        CountDownLatch countDownLatch = new CountDownLatch(poolSize);
        Lock lock = new ReentrantLock();
        int count = channel.queueDeclarePassive("eventMsgQueue").getMessageCount();
        for (int i = 0; i < count; i++) {
            GetResponse getResponse = channel.basicGet("eventMsgQueue", false);
            list.add(getResponse);
            if (count > CAPACITY && list.size() % CAPACITY == 0) {
                sendMsg(message, channel, list, countDownLatch, lock, timeMillis, i);
                list.clear();
                continue;
            }
            if (count < CAPACITY && list.size() % (CAPACITY / 100) == 0) {
                sendMsg(message, channel, list, countDownLatch, lock, timeMillis, i);
                list.clear();
                continue;
            }
            if (count < CAPACITY / 100) {
                sendMsg(message, channel, list, countDownLatch, lock, timeMillis, i);
                list.clear();
                continue;
            }

        }
        threadPoolExecutor.shutdown();
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        long t = System.currentTimeMillis();
        log.error(">>>>>>>>>>结束消费消息message>>>>，用时:{}", t - timeMillis);
    }

    private void sendMsg(Message message, Channel channel, List<GetResponse> list, CountDownLatch countDownLatch, Lock lock, long timeMillis, int i) throws Exception {
        try {
            list.stream().forEach(p -> {
                threadPoolExecutor.execute(() -> {
                    lock.lock();
                    log.info(">>>>>>>>>>>>>>>>>>消费第{}消息{},\r\n >>>>>>>>>>>>successful!", i, p.getBody());
                    countDownLatch.countDown();
                    lock.unlock();
                });
            });
            countDownLatch.await();
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>>消息{}:消费异常{}", message.getBody(), e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**    @RabbitListener(queues = {"eventMsgQueue"})
    public void getMessage(Message message, Channel channel) throws IOException {
    log.error(">>>>>>>>>>开始消费消息message:{}>>>>", message.getBody());

    long timeMillis = System.currentTimeMillis();
    int poolSize = threadPoolExecutor.getPoolSize();
    CountDownLatch countDownLatch = new CountDownLatch(poolSize);
    Lock lock = new ReentrantLock();
    try {
    threadPoolExecutor.execute(
    () -> {
    lock.lock();
    log.info("已经消费消息{},\r\n successful!!!!!!!!!!!!", message.getBody());
    countDownLatch.countDown();
    lock.unlock();
    });
    countDownLatch.await();
    } catch (Exception e) {
    log.error("消息{}:消费异常{}", message.getBody(), e.getMessage());
    } finally {
    threadPoolExecutor.shutdown();
    }
    long t = System.currentTimeMillis();
    log.error(">>>>>>>>>>结束消费消息message>>>>，用时:{}", t - timeMillis);
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
     * */
}
