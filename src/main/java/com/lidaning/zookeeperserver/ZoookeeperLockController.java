package com.lidaning.zookeeperserver;

import com.lidaning.zookeeperserver.zookeeper.lock.ZookeeperLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

@Slf4j
@RestController
@RequestMapping("/zookeeperlock")
public class ZoookeeperLockController {

    int orderNum = 1;
    CountDownLatch latch = new CountDownLatch(1);

    @GetMapping("/conrequest")
    public void conRequest() throws Exception {
        Lock lock = new ZookeeperLock();
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(lock.tryLock()){
                        log.info("current order num : "+orderNum);
                        orderNum++;
                    }else{
                        waitForLock();
                    }

                }
            }).start();
        }
        latch.countDown();
    }

    private void waitForLock() {

    }
}
