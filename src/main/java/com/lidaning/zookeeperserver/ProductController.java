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
}
