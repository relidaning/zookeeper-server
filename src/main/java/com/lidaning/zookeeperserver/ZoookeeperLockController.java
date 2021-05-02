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
    CountDownLatch latch = new CountDownLatch(1);

    @GetMapping("/conrequest")
    public void conRequest() throws Exception {
        for(int i=0;i<50;i++){
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try{
                        latch.await();
                        Lock lock = new ZookeeperLock();
                        if ( lock.lock()){
                            Thread.sleep(100);
                            log.info("current order num : "+orderNum);
                            orderNum++;
                            lock.unlock();
                        }else{
                            lock.waitForlock();
                            lock.lock();
                            Thread.sleep(100);
                            log.info("current order num : "+orderNum);
                            orderNum++;
                            lock.unlock();
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
