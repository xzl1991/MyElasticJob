/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.lock.DistributedLockController;
import com.utils.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.dangdang.ddframe.job.example.job.simple.SpringSimpleJob;

//@Configuration
@Data @Slf4j
public abstract  class BaseSimpleJobConfig implements SimpleJob,InitializingBean{

    private JobScheduler scheduler ;

    protected String cron;

    protected Integer shardingTotalCount;

    protected String shardingItemParameters;

    protected String jobName;

    protected String desc;

    private static final String SHARDING = "sharding";

    private static final String SHARD_INSTANCE = "instance";

    public static String flag = UUID.randomUUID().toString();
    @Resource
    private ZookeeperRegistryCenter regCenter;

    @Autowired
    protected ZookeeperRegistryCenter zkCenter;

    @Resource
    protected JobBuilderFactory jobBuilderFactory;

    @Resource
    private JobEventConfiguration jobEventConfiguration;

    /**
     * 返回springbatch-job实例
     * @return SpringBatchJob
     */
    protected abstract Job job();
    /**
     * 子类返回ElasticJob实体，方便jobScheduler方法调用生成对应实体
     * @return ElasticJob实例
     */
    protected abstract BaseSimpleJobConfig getBean();

    @Override
    public void execute(final ShardingContext shardingContext) {
        System.out.println(String.format("-MySimpleJob-----Thread ID: %s, 任务总片数: %s, 当前分片项: %s",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));
        log.info("开始execute调度中心分配的任务,shardingContext:{}", JSON.toJSONString(shardingContext));
        try {
            JobParameters jobParameters =  jobParameters(shardingContext);
            ((JobLauncher) SpringContextHolder.getBean("jobLauncher")).run(job(), jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            log.info("spring-batch任务已经在运行中!");
            e.printStackTrace();
        } catch (JobRestartException e) {
            log.info("spring-batch任务重启失败!");
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("spring-batch任务已经执行完成!");
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            log.info("spring-batch参数异常!");
            e.printStackTrace();
        }
    }
    /**
     * 初始化spring-batch需要的参数
     * @param shardingContext elasticJob分配的分片参数，包括分片总数和分片id对10余数
     * @return SpingBatch.JobParamters
     */
    private JobParameters jobParameters(ShardingContext shardingContext) {
        final String shardingParameter = shardingContext.getShardingParameter();
        final int shardingCount = shardingContext.getShardingTotalCount();
        final int shardingItem = shardingContext.getShardingItem();
        Map<String, JobParameter> map = new HashMap<String, JobParameter>(){
            {put("shardingParameter",new JobParameter(shardingParameter));}
            {put("shardingCount",new JobParameter(shardingCount+""));}
            {put("shardingItem",new JobParameter(shardingItem+""));}
            {put("Date", new JobParameter(System.currentTimeMillis()));}
        };
        return new JobParameters(map);
    }
//    @Bean(initMethod = "init")
//    public JobScheduler simpleJobScheduler( @Value("${elastic-job.testSkip.cron}") final String cron, @Value("${elastic-job.simpleJob.shardingTotalCount}") final int shardingTotalCount,
//                                            @Value("${elastic-job.testSkip.shardingItemParameters}") final String shardingItemParameters) {
    private JobScheduler simpleJobScheduler() {//值从子类获取
        System.out.println("simpleJobScheduler======实例化=========");
        scheduler = new SpringJobScheduler(getBean(), regCenter, getLiteJobConfiguration(getBean().getClass(), cron, shardingTotalCount, shardingItemParameters), jobEventConfiguration);
        return scheduler;
    }
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
                jobName, cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        simpleJobScheduler().init();
    }
    /**
     * 基础job监听器
     */
    public class BaseJobExecutionListener implements JobExecutionListener {

        @Override
        public void beforeJob(JobExecution jobExecution) {
            System.out.println("+++++++job==名称+++++++++");
            String jobName = jobExecution.getJobInstance().getJobName();
            int shardingItem = Integer.parseInt(jobExecution.getJobParameters().getString("shardingItem"));
            int shardingCount = Integer.parseInt(jobExecution.getJobParameters().getString("shardingCount"));
            log.info("当前任务编号：{}, 总任务数：{}", shardingItem, shardingCount);
            //重置zk跑批状态
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            String jobName = jobExecution.getJobInstance().getJobName();
            long time = (System.currentTimeMillis() - jobExecution.getStartTime().getTime()) / 1000;
            long readCount = 0, commitCount = 0, writeCount = 0;
            Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
            for(StepExecution execution : stepExecutions) {
                readCount += execution.getReadCount();
                commitCount += execution.getCommitCount();
                writeCount += execution.getWriteCount();
            }
            log.info("【job:{}】运行结束，总耗时：{}，结束状态：{}，读取数据量：{}，提交数据量：{}，写入数据量：{}", jobName, time > 60 ? (time / 60 + "m" + time % 60 + "s") : (time + "s"), jobExecution.getExitStatus(), readCount, commitCount, writeCount);
        }

        protected void syncAfterJob(){}
    }
}
