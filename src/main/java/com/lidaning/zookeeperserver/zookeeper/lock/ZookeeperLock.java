package com.lidaning.zookeeperserver.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
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


    public ZookeeperLock() {
        try{
            zooKeeper = new ZooKeeper("192.168.178.222:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                }
            });
            Stat exists = zooKeeper.exists(zkLockRoot, false);
            if(exists == null)
                zooKeeper.create(zkLockRoot, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean lock() throws KeeperException, InterruptedException {
        if(cur_node_name.equals("")){
            cur_node_name = zooKeeper.create(zkLockRoot+zkLockNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            zooKeeper.exists(cur_node_name, true);
            log.info("当前线程:"+Thread.currentThread().getName()+",创建znode节点:"+cur_node_name);
        }

        List children = zooKeeper.getChildren(zkLockRoot, false);
        Collections.sort(children);

        if(this.cur_node_name.equals(zkLockRoot+"/"+children.get(0))){
            log.info("znode:"+this.cur_node_name+" get lock, embarking work...");
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean unlock() throws KeeperException, InterruptedException {
        log.info(cur_node_name + " work finish , release the lock...");
        zooKeeper.delete(cur_node_name, 0);
        zooKeeper.close();
        return true;
    }

    @Override
    public boolean waitForlock() throws KeeperException, InterruptedException {
        List children = zooKeeper.getChildren(zkLockRoot, false);
        Collections.sort(children);

        int i = Collections.binarySearch(children, cur_node_name.replace(zkLockRoot + "/", ""));
        last_node_name = zkLockRoot + "/" + children.get(i-1);
        CountDownLatch latch = new CountDownLatch(1);
        zooKeeper.getData(last_node_name, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                log.info("当前线程"+Thread.currentThread().getName()+"触发监听, 节点为:"+watchedEvent.getPath()+"该节点上一个节点为:"+last_node_name);
                if(last_node_name.equals(watchedEvent.getPath())){
                    log.info("监听获知前一节点发生变化:"+watchedEvent.getType());
                    latch.countDown();
                }
            }
        }, new Stat());
        latch.await();
        return true;
    }

}
