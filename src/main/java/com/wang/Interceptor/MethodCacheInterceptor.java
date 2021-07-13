package com.wang.Interceptor;

import java.lang.reflect.Method;

import com.wang.annocation.RedisCache;
import com.wang.util.RedisUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * 用户登录过滤器
 * @author snw
 *
 */
public class MethodCacheInterceptor implements MethodInterceptor {
    private RedisUtil redisUtil;


    /**
     * 初始化读取不需要加入缓存的类名和方法名称
     */
    public MethodCacheInterceptor() {

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object value = null;
        String targetName = invocation.getThis().getClass().getName();
        Method method = invocation.getMethod();
        String methodName = method.getName();
        RedisCache annotation = method.getAnnotation(RedisCache.class);
        //说明当前方法不需要缓存的，
        if(annotation == null){
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        String key = getCacheKey(targetName, methodName, arguments);
        System.out.println(key);
        try {
            // 判断是否有缓存
            if (redisUtil.exists(key)) {
                System.out.println("方法名称为："+methodName+"，根据："+key+"，从缓存中获取");
                return redisUtil.get(key);
            }
            // 写入缓存
            value = invocation.proceed();
            if (value != null) {
                final String tkey = key;
                final Object tvalue = value;
                if(annotation.isCache()){
                    redisUtil.set(tkey, tvalue);

                }
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            if (value == null) {
                return invocation.proceed();
            }
        }
        return value;
    }

    /**
     * 创建缓存key
     *
     * @param targetName
     * @param methodName
     * @param arguments
     */
    private String getCacheKey(String targetName, String methodName,
                               Object[] arguments) {
        StringBuffer sbu = new StringBuffer();
        sbu.append(targetName).append("_").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                sbu.append("_").append(arguments[i]);
            }
        }
        return sbu.toString();
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
