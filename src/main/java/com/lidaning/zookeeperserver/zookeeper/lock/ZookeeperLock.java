package com.lidaning.zookeeperserver.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.*;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class ZookeeperLock implements Lock {

    private CuratorFramework zk;
    private String zkLockRoot="/zklock";
    private String zkLockNode="/zknode";
    private String cur_node_name="";
    private String watchNodename="";


    public ZookeeperLock() throws Exception {
        zk = CuratorFrameworkFactory.builder().connectionTimeoutMs(5000)
                .connectString("192.168.178.222:2181")
                .retryPolicy(new RetryNTimes(3, 3000)).build();
        ConnectionStateListener listener = new ConnectionStateListener() {

            public void stateChanged(CuratorFramework client,
                                     ConnectionState newState) {
                if (newState == ConnectionState.CONNECTED) {
                    log.info("连接成功了");
                }
            }
        };

        zk.getConnectionStateListenable().addListener(listener);
        zk.start();

        Stat exists = zk.checkExists().forPath(zkLockRoot);
        if(exists == null)
            zk.create().creatingParentsIfNeeded().forPath(zkLockRoot);
    }

    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            cur_node_name = zk.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(zkLockRoot+zkLockNode);
            List<String> nodes = zk.getChildren().forPath(zkLockRoot);
            Collections.sort(nodes);
            if(nodes.get(0).equals(cur_node_name)){
                return true;
            }else{
                log.info("cur_node_name : "+cur_node_name);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
