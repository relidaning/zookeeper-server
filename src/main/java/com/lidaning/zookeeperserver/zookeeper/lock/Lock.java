package com.lidaning.zookeeperserver.zookeeper.lock;

import org.apache.zookeeper.KeeperException;

public interface Lock {

    public boolean lock() throws KeeperException, InterruptedException;

    public boolean unlock() throws KeeperException, InterruptedException;

    public boolean waitForlock() throws KeeperException, InterruptedException;

}
