package org.example;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static jodd.util.ThreadUtil.sleep;

@Slf4j
public class RedissonMain {
    RedissonClient redissonClient;

    private static final String BIZ_NAME = "merchant";
    private static final String TABLE_NAME = "store";
    private static final String FIELD_NAME = "code";

    // 过期时间 单位：分钟
    private final Integer EXPIRE_TIME = 15;

    public RedissonMain() {
        this.init();
    }
    public void init(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        //.setPassword("123456")
        //.setDatabase(0);
        //获取客户端
        redissonClient= Redisson.create(config);
    }

    public void destory(){
        //关闭客户端
        redissonClient.shutdown();
    }
    public void method1(){
        //获取所有的key
        redissonClient.getKeys().getKeys().forEach(System.out::println);
    }

    public void method2(){
        RList<Object> clientList = redissonClient.getList(generateRedisCode("merchantcode", "tenantid"));
        clientList.addAll(new ArrayList<>());
        clientList.add("code1");
        clientList = redissonClient.getList(generateRedisCode("merchantcode", "tenantid"));
        clientList.stream().forEach(System.out::println);
    }
    // 批量查询list
    public void method3(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("单独操作");
        for (int i = 0; i <40 ; i++) {
            String code = generateRedisCode("merchantcode" + i, "tenantid");
            RList<Object> client = redissonClient.getList(code);
            System.out.println(client.size());
        }
        stopWatch.stop();
        stopWatch.start("批量操作");
        StopWatch pWatch = new StopWatch();
        pWatch.start("创建batch");
        RBatch batch = redissonClient.createBatch();
        pWatch.stop();
        pWatch.start("添加操作");
        for (int i = 0; i <40 ; i++) {
            String code = generateRedisCode("merchantcode" + i, "tenantid");
            batch.getList(code).sizeAsync();
        }
        pWatch.stop();
        pWatch.start("execute");
        BatchResult<?> batchResult = batch.execute();
        pWatch.stop();
        batchResult.stream().forEach(result-> System.out.println(result));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        System.out.println(stopWatch.shortSummary());
        System.out.println(pWatch.prettyPrint());
        System.out.println(pWatch.shortSummary());
    }

    /**
     * 为List的每个元素设置过期时间
     * 如果过期时间相同则使用 List类型
     */
    public void elementExpiredByList(){
        DateTime now = DateUtil.date();
        RList<Object> expireListTest = redissonClient.getList("expireListTest");
        for (int i = 1; i <= 5; i++) {
            String value="value"+i;
            DateTime expiredTime = DateUtil.offsetMinute(now, i);
            long expiredTimeStamp = expiredTime.getTime();
            String storedValue=expiredTimeStamp+":"+value;
            expireListTest.add(storedValue);
        }
    }

    public Object getElementFromList(String target){
        RList<String> expireTestList = redissonClient.getList("expireListTest");
        boolean findTarget = false;
        // 将过期的元素删除
        Iterator<String> iterator = expireTestList.iterator();
        while(iterator.hasNext()){
            String e = iterator.next();
            int divIndex = e.indexOf(':');
            long expireTime = Long.parseLong(e.substring(0,divIndex));
            String value = e.substring(divIndex+1);
            if (value.equals(target)){
                findTarget=true;
            }
            if (!expired(expireTime)){
                break;
            }
            System.out.println(value+" 过期了,删除!");
            iterator.remove();
        }
        // 如果list size = 0; 则将list删除
        if (expireTestList.isEmpty()) {
            expireTestList.delete();
        }
        // 如果查找元素不在上面过期的元素中，则继续查找元素
        if (!expireTestList.isEmpty()&&!findTarget){
            for(String e : expireTestList){
                int divIndex = e.indexOf(':');
                String value = e.substring(divIndex+1);
                if (value.equals(target)){
                    return e;
                }
            }
        }
        return null;
    }
    /**
     * 如果过期时间不同，则使用ZSet类型
     */
    public void elementExpiredByZSet(){

    }


    // 使用redisson的来元素淘汰功能来为Hash元素设置过期时间




    /*
  生成redisKey
   */
    private String generateRedisCode(String code, String tenantId){
        return tenantId+":"+BIZ_NAME+":"+TABLE_NAME+":"+FIELD_NAME+":"+code;
    }
    /**
     * 删除缓存
     */
    private void removeCache(String code, String tenantId){
        RKeys clientKeys = redissonClient.getKeys();
        String redisKey = generateRedisCode(code, tenantId);
        long deleteNum = clientKeys.delete(redisKey);
        if (deleteNum==0)
            log.info("delete Cache failed");
    }


    private Boolean expired(long timestamp1){
        DateTime date1 = DateUtil.date(timestamp1);
        DateTime date2 = DateUtil.date();
        return DateUtil.compare(date1, date2)<0;
    }


    /*
    1691997988790:value1
    1691998048790:value2
    1691998108790:value3
    1691998168790:value4
    1691998228790:value5
     */
    public static void main(String[] args) {
        RedissonMain redissonMain = new RedissonMain();
        redissonMain.elementExpiredByList();
        Object value1 = redissonMain.getElementFromList("value1");
        System.out.println("value1 = "+value1);
        System.out.println("sleep 60s");
        sleep(60000);
        log.info("睡醒了");
        value1 = redissonMain.getElementFromList("value1");
        log.info("value1:{}",value1);

        Object value4 = redissonMain.getElementFromList("value4");
        log.info("value4:{}",value4);
        redissonMain.destory();
    }

}