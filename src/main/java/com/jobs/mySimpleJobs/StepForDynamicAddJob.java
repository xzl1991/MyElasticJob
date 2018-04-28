package com.jobs.mySimpleJobs;

import com.alibaba.fastjson.JSONObject;
import com.config.BaseRead;
import com.config.BaseSimpleJobConfig;
import com.config.BaseStep;
import com.config.BaseWrite;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 请假step
 * @author: guoxiaoyong@gomeholdings.com
 * @date: 2017/12/7 上午10:25
 */
@Slf4j
@Component
@Scope("prototype")
public class StepForDynamicAddJob extends BaseStep {

    @Resource
    protected JobBuilderFactory jobBuilderFactory;


    @Resource
    private JobEventConfiguration jobEventConfiguration;


    @Autowired
    protected ZookeeperRegistryCenter zkCenter;


    private Date businessDate;

    public StepForDynamicAddJob(@Value("${elastic-job.dynamicAddJob.interval}") String[] interval) {
        super(interval);
    }

    @Override
    public Step buildStep() {
//        return stepBuilderFactory.get("dynamicAddJob-step")
//                .listener(new BaseExecutionListener("dynamicAddJob", "dynamicAddJob-step-dynamicAddJob"))
//                .tasklet(new Task())
//                .build();
        return stepBuilderFactory.get("dynamicAddJob-step").chunk(2)
                .reader(new Read())
                .writer(new Writer())
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    class Task implements Tasklet {

        @Override
        @Transactional
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            // 获取 任务列表
//            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-job.xml");
//            ZookeeperRegistryCenter zookeeperRegistryCenter = context.getBean(ZookeeperRegistryCenter.class);
//            for (int i = 0; i < 4; i++) {
//                JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder("smsTaskJob-dynamic-" + i, "0/40 * * * * ?", 1).build();
//                SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(coreConfig, MyElasticJob.class.getCanonicalName());
//                JobScheduler jobScheduler = new JobScheduler(regCenter, LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build());
//                jobScheduler.init();
//            }
            return RepeatStatus.FINISHED;
        }

    }
}
    class Read extends BaseRead<String> {
        @Override
        protected void doReadPage() {
//            super.doReadPage();
            results = new ArrayList<>();
            results.add("good1");
            results.add("good2");
            results.add("good3");
            results.add("bad4");
            results.add("good5");
            results.add("good6");
            results.add("bad7");
            results.add("good8");
        }
    }
    class Writer<O> extends BaseWrite<String> {
        @Resource
        private ZookeeperRegistryCenter regCenter;
        @Override
        public void write(List<? extends String> list) throws Exception {
            System.out.println("write ========smsTaskJob-dynamic=====================================================");
//            super.write(list);
//            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-job.xml");
            ZookeeperRegistryCenter zookeeperRegistryCenter = SpringContextHolder.getBean(ZookeeperRegistryCenter.class);
            for ( int i = 0; i<4; i++){
                final String name  = "smsTaskJob-dynamic-" + i;
                JobCoreConfiguration coreConfig;
                JSONObject param = new JSONObject();
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(3);
                jsonArray.add(5);
                param.put("GROUP","Mj");
                param.put("LOAN_CODE","Mj");
                param.put("OVERDUEDAY", jsonArray);
                if (i == 0){
                     coreConfig = JobCoreConfiguration.newBuilder(name, "0/40 * * * * ?", 1)
                            .jobParameter("任务参数,MJ，MYF,3,5:"+new Date()+":"+System.currentTimeMillis()).build();
                }else {
                     coreConfig = JobCoreConfiguration.newBuilder(name, "0/40 "+i+" * * * ?", 1)
                            .jobParameter("任务参数,MJ，MYF,3,5:"+new Date()).build();
                }
                SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(coreConfig,MyElasticJob.class.getCanonicalName());
                LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build();
                JobScheduler jobScheduler = new SpringJobScheduler(SpringContextHolder.getBean(MyElasticJob.class), zookeeperRegistryCenter, liteJobConfiguration, SpringContextHolder.getBean(JobEventConfiguration.class));
                jobScheduler.init();
            }
    }
}
