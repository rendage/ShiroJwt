package com.wang.util;

import com.alibaba.fastjson.parser.Feature;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.alibaba.fastjson.parser.Feature.AllowComment;

@Data
class ABC implements ABC2{
    String orderNo;
    public ABC(){}
    public ABC(String orderNo){
        this.orderNo=orderNo;
    }
}
@Data
class ABC1{
    String orderNo;
    public ABC1(){}
    public ABC1(String orderNo){
        this.orderNo=orderNo;
    }

}
// JAVA1.8新特性 接口可以实现default 关键字形容的具体方法
interface  ABC2{
    default void dotest() {
        System.out.println("this is test default method ");
    }
}

public class AsynAndJavafunction {



        public static void main(String[] args) {

            // 异步测试
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            ABC order = new ABC();
          //  order.dotest();
            ExecutorService executorService1 = Executors.newFixedThreadPool(1);
            order.setOrderNo("first");
            executorService.execute(() -> doSomeThing(order));
            order.setOrderNo("second");
            executorService.execute(() -> doSomeThing(order));
            System.out.println("sdain".hashCode());
            // function 函数测试
            Function<String,ABC> function1= p -> { return new ABC(p); };
            System.out.println(doFunctioninvoke("test function method ", function1));
            System.out.println(doFunctioninvoke("test function method ", function1));
            System.out.println(doFunctionAndThenInvoke("test function andThen method ", function1));
            Function<String, String> function = a -> a + " Jack!";
            System.out.println(function.apply("Hello")); // Hello Jack!

            // Predicate测试
            Predicate<Integer> predicate = number -> number != 0;
            predicate = predicate.and(number -> number >= 11);
            System.out.println(predicate.test(10));    //true

            // 二进制输出
            System.out.println((byte)Integer.parseInt("00000100", 2));
            System.out.println(Feature.isEnabled(1<<3,Feature.AllowSingleQuotes));
            String[] a=new String[2];

            List<String> list = new ArrayList<String>();
            System.out.println(a);
            List<String> objects = Arrays.asList(a);
            objects.add("sasa");
        }

        public static ABC doFunctioninvoke(String str,Function<String,ABC> function){
            return function.apply(str);
        }

        public static ABC1 doFunctionAndThenInvoke(String str,Function<String,ABC> functionAndThen){
            Function<ABC,ABC1> function= p -> { return new ABC1(p.getOrderNo()); };
            return functionAndThen.andThen(function).apply(str);
        }

        private static void doSomeThing(ABC order) {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(order.getOrderNo());
        }


    }
