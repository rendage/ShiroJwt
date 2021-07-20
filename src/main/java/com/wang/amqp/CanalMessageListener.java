package com.wang.amqp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description canal同步mysql数据库数据到redis
 * @date 2021/6/24 15:27
 * @author renxz
 */
@Component
public class CanalMessageListener {

    private static final Logger log = LoggerFactory.getLogger(CanalMessageListener.class);

   // @Autowired
   // private UserService userService;

   // @RabbitListener(queues = {AmqpConstant.CANAL_QUEUE})
    public void execute(Message message, Channel channel) throws IOException {
        try {
            HashMap<String, Object> map = (HashMap) JSON.parseObject(new String(message.getBody()), Map.class);
            if (null == map) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                return;
            }
            // 获取表名
            Object table = map.get("table");
            String tableName = table == null ? "" : table.toString();
            // 获取数据库数据操作类型
            String operType = Optional.ofNullable((String) map.get("type")).orElse("");
       //     if (!TableContants.OPER_TYPE_DELETE.equalsIgnoreCase(operType) && !TableContants.OPER_TYPE_UPDATE.equalsIgnoreCase(operType) && !TableContants.OPER_TYPE_INSERT.equalsIgnoreCase(operType)) {
       //         channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        //        return;
       //     }
            List<JSONObject> alist = JSON.parseObject(map.get("data").toString(), List.class);
            if (null == alist || alist.size() <= 0) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                return;
            }
            // 加入缓存
        //    userService.addRedisCache(tableName, operType, alist.get(0));
            // 消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            log.error("CanalMessageListener | execute |异常:原因{}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, false);
        }
    }
}


