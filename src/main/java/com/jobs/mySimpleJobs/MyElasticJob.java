package com.jobs.mySimpleJobs;

import com.config.BaseSimpleJobConfig;
import com.utils.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther xzl on 14:10 2018/2/9
 */
@Component
@ConfigurationProperties(prefix = "elastic-job.testSkip")
@Slf4j

public class MyElasticJob extends BaseSimpleJobConfig{
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public MyElasticJob(){
        this.jobName = "测试-skip";
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
}
