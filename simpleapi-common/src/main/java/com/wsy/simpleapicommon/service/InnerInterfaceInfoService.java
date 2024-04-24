package com.wsy.simpleapicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsy.simpleapicommon.model.entity.InterfaceInfo;

import java.util.List;

/**
* @author 15790
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-04-17 13:33:18
*/
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 网关获取所有接口列表
     * @return
     */
    List<InterfaceInfo> getInterfaceInfoList();
}
