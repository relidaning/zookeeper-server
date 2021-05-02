package com.lidaning.zookeeperserver.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/curator")
public class CuratorFrameworkController {

    int orderNum = 1;

    @GetMapping("/conrequest")
    public String conrequest() throws Exception {
        CuratorFramework zk = CuratorFrameworkFactory.newClient("192.168.178.222:2181", 5000,
                3000, new ExponentialBackoffRetry(1000, 3));
        zk.start();
        InterProcessMutex process = new InterProcessMutex(zk, "/zklock/zknode");
        CountDownLatch latch = new CountDownLatch(1);
        try{
            for(int i=0;i<100;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await();
                            process.acquire();
                            Thread.sleep(100);
                            log.info("current order num : "+orderNum);
                            orderNum++;
                            process.release();
                        }catch (Exception e){

                        }

                    }
                }).start();
            }
            latch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return null;
    }
}
