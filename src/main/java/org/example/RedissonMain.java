package org.example;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public static void main(String[] args) {
        RedissonMain redissonMain = new RedissonMain();
        redissonMain.method3();
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

}