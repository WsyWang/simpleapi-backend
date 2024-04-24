package com.wsy.simpleapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsy.simpleapi.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wsy.simpleapicommon.model.entity.InterfaceInfo;
import com.wsy.simpleapicommon.model.vo.InterfaceInfoVO;

/**
* @author 15790
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-04-17 13:33:18
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo);

    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage);
}
