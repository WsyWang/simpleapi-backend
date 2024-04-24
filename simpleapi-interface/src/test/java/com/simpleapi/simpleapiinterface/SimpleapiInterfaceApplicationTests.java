package com.simpleapi.simpleapiinterface;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.wsy.simpleapiclientsdk.client.SimpleApiClient;
import com.wsy.simpleapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
class SimpleapiInterfaceApplicationTests {

    @Resource
    private SimpleApiClient simpleApiClient;

    @Test
    void contextLoads() {
        System.out.println(simpleApiClient.getNameByGet("nihao"));
        System.out.println(simpleApiClient.getNameByPost("nihao"));
        User user = new User();
        user.setName("wsy");
        System.out.println(simpleApiClient.getUserNameByPost(user));

    }

    @Test
    void test() {
        String jsonStr = "{'name': 'username', 'name2' : 'name2'}";
        System.out.println(JSONUtil.toBean(jsonStr, HashMap.class));
    }

}
