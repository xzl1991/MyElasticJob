package com.jobs.mySimpleJobs;

import com.config.BaseRead;
import com.config.BaseStep;
import com.config.BaseWrite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther xzl on 11:15 2018/3/5
 */
@Slf4j
@Component
//@Scope("prototype")
public class MyStep extends BaseStep {
    public MyStep(@Value("${elastic-job.simpleJob.interval}") String[] interval){
        super(interval);
    }
    @Override
    public Step buildStep() {
        return stepBuilderFactory.get("leaveJob-step")
                .listener(new BaseExecutionListener("leaveJob", "leaveJob-step-stepForLeave"))
                .chunk(2)
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
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step==> Tasklet---------------------------------");
            return null;
        }
    }

    class Read extends BaseRead<String>{
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
        @Override
        public void write(List<? extends String> list) throws Exception {
//            super.write(list);
           for (String str : list){
               if (str.indexOf("bad")>-1){
                   System.out.println("！！！！！！！！！！！！！！！！！=错误数据重试："+str);
                    throw new RuntimeException("错误数据----");
               }else {
                   System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~："+str);
               }
           }
        }

//        @Override
//        public void write(List<?> list) throws Exception {
//
//        }
    }
}
