package com.wsy.simpleapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wsy.simpleapi.mapper.InterfaceInfoMapper;
import com.wsy.simpleapi.service.InterfaceInfoService;
import com.wsy.simpleapicommon.model.entity.InterfaceInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InnerInterfaceInfoServiceImplTest {

    @Resource
    InterfaceInfoMapper interfaceInfoMapper;

    @Test
    void getInterfaceInfoList() {
        List<InterfaceInfo> interfaceInfos = interfaceInfoMapper.selectList(Wrappers.emptyWrapper());
        System.out.println(interfaceInfos);
    }
}