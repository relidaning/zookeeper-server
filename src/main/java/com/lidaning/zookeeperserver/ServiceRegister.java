package com.lidaning.zookeeperserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * @author Administrator
 * @since 2021-4-28
 */
@Slf4j
public class ServiceRegister {
    private static final String BASE_SERVICE = "/zookeeper";

    private static final String SERVICE_NAME = "/server";

    public static void reister(String address,int port){
        String path = BASE_SERVICE+SERVICE_NAME;
        try {

            ZooKeeper zooKeeper = new ZooKeeper("192.168.178.128:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    log.info("register receive event :" + watchedEvent.getType().name());
                }
            });

            Stat exists = zooKeeper.exists(BASE_SERVICE+SERVICE_NAME,false);

            if(exists == null){
                zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String server_path = address+":"+port;

            zooKeeper.create(path+"/child",server_path.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("产品服务注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
