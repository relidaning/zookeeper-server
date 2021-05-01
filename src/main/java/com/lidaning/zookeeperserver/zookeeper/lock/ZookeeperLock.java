package com.lidaning.zookeeperserver.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperLock implements Lock {

    private ZooKeeper zooKeeper = null;
    private String zkLockRoot="/zklock";
    private String zkLockNode="/zknode";
    private String cur_node_name="";
    private String last_node_name="";
    private CountDownLatch latch=null;


    public ZookeeperLock() throws Exception {

        zooKeeper = new ZooKeeper("192.168.178.222:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                log.info("连接成功了");
            }
        });
        Stat exists = zooKeeper.exists(zkLockRoot, false);
        if(exists == null)
            zooKeeper.create(zkLockRoot, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Override
    public boolean lock() throws KeeperException, InterruptedException {
        if(cur_node_name.equals("")){
            cur_node_name = zooKeeper.create(zkLockRoot+zkLockNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }

        List children = zooKeeper.getChildren(zkLockRoot, false);
        Collections.sort(children);

        if(cur_node_name.equals(zkLockRoot+"/"+children.get(0))){
            log.info("znode:"+cur_node_name+" get lock, embarking work...");
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean unlock() throws KeeperException, InterruptedException {
        log.info("finish work, release lock...");
        zooKeeper.delete(cur_node_name, 0);
        return true;
    }

    @Override
    public boolean waitForlock() throws KeeperException, InterruptedException {
        latch=new CountDownLatch(1);
        List children = zooKeeper.getChildren(zkLockRoot, false);
        Collections.sort(children);

        int i = Collections.binarySearch(children, cur_node_name.replace(zkLockRoot + "/", ""));
        last_node_name = zkLockRoot + "/" + children.get(i-1);
        log.info("znode:"+cur_node_name+" watch the node:"+last_node_name+", wait for him finished...");
        zooKeeper.register(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(last_node_name.equals(watchedEvent.getPath())){
                    log.info("监听获知前一节点发生变化:"+watchedEvent.getType());
                    latch.countDown();
                }
            }
        });
        latch.await();
        return false;
    }

}
