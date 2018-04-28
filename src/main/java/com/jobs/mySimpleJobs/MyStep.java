package com.jobs.mySimpleJobs;

import com.config.BaseRead;
import com.config.BaseStep;
import com.config.BaseWrite;
import com.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther xzl on 11:15 2018/3/5
 */
@Slf4j
@Component
@Scope("prototype")
public class MyStep extends BaseStep {
    public MyStep(@Value("${elastic-job.simpleJob.interval}") String[] interval){
        super(interval);
    }
    @Override
    public Step buildStep() {
        System.out.println("====step========");
        BaseExecutionListener listener = new BaseExecutionListener("leaveJob", "leaveJob-step-stepForLeave");
//        listener.beforeStep();
        Step  step =  stepBuilderFactory.get("leaveJob-step")
                .listener(new BaseExecutionListener("leaveJob======", "leaveJob-step-stepForLeave"))
                .chunk(2)
                .reader(new Read())
                .processor(new Process())
                .writer(new Writer())
                .faultTolerant()
                .skipLimit(2)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(Exception.class)
                .taskExecutor(SpringContextHolder.getBean(ThreadPoolTaskExecutor.class))
                .build();
        System.out.println("step===状态信息----");
        return step;

    }

    class Process<I, O>  implements ItemProcessor<I, O>,ItemProcessListener<I, O> {
        @Override
        public void beforeProcess(I i) {

        }

        @Override
        public void afterProcess(I i, O o) {

        }

        @Override
        public void onProcessError(I i, Exception e) {

        }

        @Override
        public O process(I i) throws Exception {
            return null;
        }
//        @Override
        public String process(String value) throws Exception {
//            log.info("同步案件编号为【{}】的案件数据...", ermasCaseMain.getAcctNbr());
            //已经知道具体的模板类型 --- 怎么可以不重复查询
            System.out.println("process==*********************************8=="+value);
            return value + 0;
        }
    }
    class Task implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step==> Tasklet---------------------------------");
            return null;
        }
    }

    class Read extends BaseRead<String>{
        Map param = null;
        @Override
        public void beforeStep(StepExecution stepExecution) {
            param = stepExecution.getJobExecution().getJobParameters().getParameters();
        }
        @Override
        protected void doReadPage() {
            param.isEmpty();
            System.out.println("参数："+param);
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
            System.out.println("write =============================================================");
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
