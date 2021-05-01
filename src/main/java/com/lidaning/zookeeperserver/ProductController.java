package com.lidaning.zookeeperserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @since 2021-4-28
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    DiscoveryClient discoveryClient;

    @RequestMapping("/getInstance")
    public String getInstance(){
        //获取实例化的注册节点
        List<ServiceInstance> list = discoveryClient.getInstances("zookeeper-server");

        //获取实例化的服务
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0 ) {
            sb.append(list.get(0).getUri()+",");
        }
        return "hello world  "+sb.toString();
    }
}
