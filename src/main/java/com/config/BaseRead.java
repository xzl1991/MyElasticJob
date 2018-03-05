package com.config;

import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Configuration;

/**
 * @auther xzl on 11:08 2018/3/2
 */
@Configuration
 public abstract class BaseRead<T> extends MyBatisPagingItemReader<T> implements ItemReadListener,StepExecutionListener {
    @Override
    public void beforeRead() {

    }
    @Override
    protected void doReadPage() {
        System.out.println("********=====读取====*************");
    }
    @Override
    public void afterRead(Object o) {
        System.out.println("********=====读取~~~~结束====*************");
    }

    @Override
    public void onReadError(Exception e) {

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }


 }
