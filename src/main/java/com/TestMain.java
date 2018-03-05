//package com;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import com.utils.PIDUtil;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
//
///**
// * @auther xzl on 14:22 2018/2/8
// */
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
//@Slf4j
//public class TestMain {
//    public static void main(String[] args) {
//        SpringApplication.run(TestMain.class,args);
//        //获取运行时应用本身进程ID
//        int pid = PIDUtil.getPID();
//        log.info("ermas current pid is : {}",pid);
//    }
//}
