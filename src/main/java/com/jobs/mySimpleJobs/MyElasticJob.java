package com.jobs.mySimpleJobs;

import com.alibaba.fastjson.JSON;
import com.config.BaseSimpleJobConfig;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.utils.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther xzl on 14:10 2018/2/9
 */
@Component
@ConfigurationProperties(prefix = "elastic-job.testSkip")
@Slf4j
public class MyElasticJob extends BaseSimpleJobConfig{

    @Override
    protected BaseSimpleJobConfig getBean() {
        return this;
    }
    @Override
    protected Job job() {
        return jobBuilderFactory.get(jobName)
                .listener(new BaseJobExecutionListener())
                .incrementer(new RunIdIncrementer())
                .flow(SpringContextHolder.getBean(MyStep.class).buildStep())
                .end()
                .build();
    }

    @Override
    protected Job job(String name) {
        if (StringUtils.isEmpty(jobName)){
            this.jobName = name;
        }
        System.out.println("getJob----------------");
        return jobBuilderFactory.get(name)
                .listener(new BaseJobExecutionListener())
                .incrementer(new RunIdIncrementer())
                .flow(SpringContextHolder.getBean(MyStep.class).buildStep())
                .end()
                .build();
    }

    @Override
    public void execute(final ShardingContext shardingContext) {
        System.out.println(String.format("-子类的execute----Thread ID: %s, 任务总片数: %s, 当前分片项: %s",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));
        log.info("开始execute调度中心分配的任务,shardingContext:{}", JSON.toJSONString(shardingContext));
        try {
            JobParameters jobParameters =  jobParameters(shardingContext);
            ((JobLauncher) SpringContextHolder.getBean("jobLauncher")).run(job(shardingContext.getJobName()), jobParameters);
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
}
