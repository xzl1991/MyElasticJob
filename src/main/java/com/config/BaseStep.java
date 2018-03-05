package com.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @auther xzl on 11:06 2018/3/5
 */
public abstract class BaseStep {

//    @Autowired
//    protected ZookeeperRegistryCenter zkCenter;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    protected int write = 128;

    protected int read = 256;

    protected int process = 256;

    public BaseStep(String[] interval) {
        if (interval != null) {
            if (interval.length > 0) {
                read = Integer.parseInt(interval[0]);
            }
            if (interval.length > 1) {
                process = Integer.parseInt(interval[1]);
            }
            if (interval.length > 2) {
                write = Integer.parseInt(interval[2]);
            }
        }
    }
    public abstract Step buildStep();

    protected class BaseExecutionListener implements org.springframework.batch.core.StepExecutionListener {
        private String stepName;

        private String jobName;

//        private WaitModel waitModel;

        public BaseExecutionListener(String jobName, String stepName) {
            this.jobName = jobName;
            this.stepName = stepName;
        }

//        public BaseExecutionListener(String jobName, String stepName, WaitModel waitModel) {
//            this.jobName = jobName;
//            this.stepName = stepName;
//            this.waitModel = waitModel;
//        }
        @Override
        public void beforeStep(StepExecution stepExecution) {

        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            return null;
        }
    }
}
