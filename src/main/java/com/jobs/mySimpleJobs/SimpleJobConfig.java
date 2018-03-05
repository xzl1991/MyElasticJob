//package com.jobs.mySimpleJobs;
//
//import com.dangdang.ddframe.job.api.ShardingContext;
//import com.dangdang.ddframe.job.api.simple.SimpleJob;
//import com.dangdang.ddframe.job.config.JobCoreConfiguration;
//import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
//import com.dangdang.ddframe.job.event.JobEventConfiguration;
//import com.dangdang.ddframe.job.lite.api.JobScheduler;
//import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
//import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
//import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
///**
// * @auther xzl on 14:51 2018/3/5
// */
//@Component
//public class SimpleJobConfig implements SimpleJob {
//    private JobScheduler scheduler ;
//
//    //    @Resource
////    private JobBuilderFactory jobs;
////    @Resource
////    private StepBuilderFactory steps;
//    @Override
//    public void execute(ShardingContext shardingContext) {
//        System.out.println(String.format("-MySimpleJob-----Thread ID: %s, 任务总片数: %s, 当前分片项: %s",
//                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));
//    }
//
////    @Override
////    public void afterPropertiesSet() throws Exception {
////        scheduler.init();
////    }
//
//    @Resource
//    private ZookeeperRegistryCenter regCenter;
//
//    @Resource
//    private JobEventConfiguration jobEventConfiguration;
//
//
////    @Bean
////    public SimpleJob simpleJob() {
////        return new SimpleJob() {
////            @Override
////            public void execute(ShardingContext shardingContext) {
////                System.out.println(String.format("-MyS****impleJob-----Thread ID: %s, 任务总片数: %s, 当前分片项: %s",
////                        Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));
////            }
////        };
////    }
//
//    @Bean(initMethod = "init")
//    public JobScheduler simpleJobScheduler(@Value("${simpleJob.cron}") final String cron, @Value("${simpleJob.shardingTotalCount}") final int shardingTotalCount,
//                                           @Value("${simpleJob.shardingItemParameters}") final String shardingItemParameters) {
//        System.out.println("simpleJobScheduler======实例化=========");
//        scheduler = new SpringJobScheduler(this, regCenter, getLiteJobConfiguration(this.getClass(), cron, shardingTotalCount, shardingItemParameters), jobEventConfiguration);
//        return scheduler;
//    }
//
//    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
//        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
//                "测试--任务名称", cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
//    }
//}
