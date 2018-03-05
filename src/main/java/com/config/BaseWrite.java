package com.config;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @auther xzl on 13:21 2018/3/5
 */
public class BaseWrite<T> implements ItemWriter<T>, ItemWriteListener<T> {
    @Override
    public void beforeWrite(List<? extends T> list) {
        System.out.println("----开始读取数据==================--");
    }

    @Override
    public void afterWrite(List<? extends T> list) {

    }

    @Override
    public void onWriteError(Exception e, List<? extends T> list) {

    }

    @Override
    public void write(List<? extends T> list) throws Exception {

    }
}
