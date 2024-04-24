package com.simpleapi.simpleapiinterface.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class ApiController {

    @GetMapping("/love/words")
    public Object loveWords() {
        String res = HttpUtil.get("https://api.uomg.com/api/rand.qinghua");
        return res;
    }
}
