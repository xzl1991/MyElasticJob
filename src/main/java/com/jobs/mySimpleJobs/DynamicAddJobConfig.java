package com.jobs.mySimpleJobs;

import com.alibaba.fastjson.JSON;
import com.config.BaseSimpleJobConfig;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.utils.SpringContextHolder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 动态添加短信任务
 * @author: guoxiaoyong@gomeholdings.com
 * @date: 2017/12/7 上午10:24
 */
@Component
@ConfigurationProperties(prefix = "elastic-job.dynamicAddJob")
public class DynamicAddJobConfig extends BaseSimpleJobConfig {
    @Override
    protected Job job(String jobName) {
        return null;
    }

    @Override
    protected BaseSimpleJobConfig getBean() {
        return this;
    }

    @Override
    protected Job job() {
        return jobBuilderFactory.get(jobName)
                .listener(new BaseJobExecutionListener())
                .incrementer(new RunIdIncrementer())
                .flow(SpringContextHolder.getBean(StepForDynamicAddJob.class).buildStep())
                .end()
                .build();
    }

//    @Override
//    protected Job job(String jobName) {
//        return jobBuilderFactory.get(jobName)
//                .listener(new BaseJobExecutionListener())
//                .incrementer(new RunIdIncrementer())
//                .flow(SpringContextHolder.getBean(StepForDynamicAddJob.class).buildStep())
//                .end()
//                .build();
//    }

}
