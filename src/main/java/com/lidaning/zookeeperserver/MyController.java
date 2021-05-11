package com.lidaning.zookeeperserver;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @since 2021-4-30
 */
@RestController
@RequestMapping("/myController")
public class MyController {

    private static int stock = 100;

    /**
     * 测试连接zk
     */
    @GetMapping("/test")
    public void test() {
        //连接zookeeper
        CuratorFramework cf = CuratorFrameworkFactory.newClient("192.168.178.128:2181", 5000, 4000,
                new ExponentialBackoffRetry(1000, 3));
        cf.start();
        //互斥锁创建(创建的是zookeeper的有序临时节点)
        InterProcessMutex lock = new InterProcessMutex(cf, "/testlocks");
        //测试高并发扣减库存
        testConcurrent(lock);
    }

    //100个线程同时扣减库存
    public static final int threadNum = 100;
    /*
     * 测试高并发扣减库存 .
     */
    public static void testConcurrent(InterProcessMutex lock) {
        //定义一个计数器
        CountDownLatch cdl = new CountDownLatch(1);
        for (int a = 0; a < threadNum; a++) {
            Thread thread = new Thread(new MyRunable(cdl,lock));
            thread.start();
        }
        //计数器减一，总共就1，减一等于0，这个时候所有的线程开始同时执行，模拟高并发
        cdl.countDown();
    }

    //定义runnable
    public static class MyRunable implements Runnable {
        private final CountDownLatch countDownLatch;
        InterProcessMutex lockNew;
        public MyRunable(CountDownLatch cdl,InterProcessMutex lock) {
            countDownLatch = cdl;
            lockNew=lock;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                //等待在这里，直到计数器数值为0开始执行
                countDownLatch.await();
                //获取锁
                lockNew.acquire();
                //扣减库存
                stock = stock - 1;
                //释放锁
                lockNew.release();
                //打印当前线程名称，库存剩余多少
                System.out.println("threadName="+Thread.currentThread().getName()+",current stock=" + stock);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
