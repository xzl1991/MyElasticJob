package com.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by xlizy on 2017/6/22.
 */
@Configuration
public class SpringBatchConfig {


    @Bean(name = "mapJobRepositoryFactoryBean")
    public MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean(){
        MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean();
        return factoryBean;
    }

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor (){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(32);//线程池维护线程的最少数量
        taskExecutor.setMaxPoolSize(512);//最大线程数
        taskExecutor.setQueueCapacity(1024);//队列大小
        taskExecutor.setKeepAliveSeconds(60);//某线程空闲超过这个时间，就回收该线程
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean("jobLauncher")
    public JobLauncher jobLauncher(@Qualifier("mapJobRepositoryFactoryBean") MapJobRepositoryFactoryBean factoryBean,
                                   @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor taskExecutor) throws Exception {

        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(taskExecutor);
        jobLauncher.setJobRepository(factoryBean.getObject());
        return jobLauncher;
    }

    @Bean("jobOperator")
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer,
                                         @Qualifier("jobLauncher") JobLauncher jobLauncher,
                                         @Qualifier("mapJobRepositoryFactoryBean") MapJobRepositoryFactoryBean factoryBean) throws Exception {
        MapJobRegistry jobRegistry = new MapJobRegistry();
        SimpleJobOperator factory = new SimpleJobOperator();
        factory.setJobExplorer(jobExplorer);
        factory.setJobLauncher(jobLauncher);
        factory.setJobRegistry(jobRegistry);
        factory.setJobRepository(factoryBean.getObject());
        return factory;
    }

    @Bean("jobBuilderFactory")
    public JobBuilderFactory jobBuilderFactory(@Qualifier("mapJobRepositoryFactoryBean")MapJobRepositoryFactoryBean factoryBean ) throws Exception {
        JobBuilderFactory factory = new JobBuilderFactory(factoryBean.getObject());
        return factory;
    }

    @Bean("stepBuilderFactory")
    public StepBuilderFactory stepBuilderFactory(@Qualifier("mapJobRepositoryFactoryBean") MapJobRepositoryFactoryBean factoryBean,
                                                 @Qualifier("transactionManager")DataSourceTransactionManager transactionManager) throws Exception {
        StepBuilderFactory stepBuilderFactory = new StepBuilderFactory(factoryBean.getObject(),transactionManager);
        return stepBuilderFactory;
    }

}
