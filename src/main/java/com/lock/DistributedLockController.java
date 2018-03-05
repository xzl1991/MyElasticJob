package com.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * @auther xzl on 17:05 2018/2/8
 * 分布式锁控制器
 */
@Slf4j
@RequiredArgsConstructor
public class DistributedLockController {
    private InterProcessMutex ipm;
    public void acquire(){
        if (ipm != null) {
            try {
                ipm.acquire();
            } catch (Exception e) {
                log.error("获取分布式锁异常 msg:{}",e.getMessage());
            }
        }
    }

    public void release() throws Exception {
        if (ipm != null) {
            ipm.release();
        }
        //if (client != null) {
        //    client.close();
        //}
    }
}
