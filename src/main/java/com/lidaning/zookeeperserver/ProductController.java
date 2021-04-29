package com.lidaning.zookeeperserver;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @since 2021-4-28
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/getProduct")
    public Map getProduct(@RequestBody Map entity){
        Map map = new HashMap();
        map.put("id",entity.get("id"));
        map.put("name","你好");
        return map;
    }

    /*@RequestMapping("/getInstance")
    public String getInstance(@RequestBody Map entity){
        //获取实例化的注册节点
        List<ServiceInstance> list = discoveryClient.getInstances("CONSUL-CLIENT");

        //获取实例化的服务
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0 ) {
            sb.append(list.get(0).getUri()+",");
        }
        return "hello world  "+sb.toString();
    }*/
}
