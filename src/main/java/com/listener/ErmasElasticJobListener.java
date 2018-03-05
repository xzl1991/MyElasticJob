//package com.listener;
//
//import com.alibaba.fastjson.JSON;
//import com.dangdang.ddframe.job.executor.ShardingContexts;
//import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @auther xzl on 15:53 2018/2/8
// * elastic-Job 监听
// * 用于记录作业是否执行，以及清除资源
// */
//@Slf4j
//public class ErmasElasticJobListener implements ElasticJobListener {
//    @Override
//    public void beforeJobExecuted(ShardingContexts shardingContexts) {
//        System.out.println("接收到了elastic-Job发送的执行任务命令==="+JSON.toJSONString(shardingContexts));
//        log.info("接收到了elastic-Job发送的执行任务命令,shardingContexts:{}", JSON.toJSONString(shardingContexts));
//    }
//
//    @Override
//    public void afterJobExecuted(ShardingContexts shardingContexts) {
//        // 调度结束(不一定代表作业结束，因为spring-batch是异步执行)
//        System.out.println("调度结束==="+JSON.toJSONString(shardingContexts));
//        log.info("调度结束,shardingContexts:{}",JSON.toJSONString(shardingContexts));
//    }
//}
