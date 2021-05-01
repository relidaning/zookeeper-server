package com.lidaning.zookeeperserver;

import com.lidaning.zookeeperserver.zookeeper.lock.Lock;
import com.lidaning.zookeeperserver.zookeeper.lock.ZookeeperLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

@Slf4j
@RestController
@RequestMapping("/zookeeperlock")
public class ZoookeeperLockController {

    int orderNum = 1;
    Lock lock = null;
    CountDownLatch latch = new CountDownLatch(1);

    @GetMapping("/conrequest")
    public void conRequest() throws Exception {
        lock = new ZookeeperLock();
        for(int i=0;i<2;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        latch.await();
                        if ( lock.lock()){
                            log.info("current order num : "+orderNum);
                            orderNum++;
                            lock.unlock();
                        }else{
                            lock.waitForlock();
                        }
                    }catch(Exception e){

                    }finally{

                    }
                }
            }).start();
        }
        latch.countDown();
    }

    private void waitForLock() {

    }
}
