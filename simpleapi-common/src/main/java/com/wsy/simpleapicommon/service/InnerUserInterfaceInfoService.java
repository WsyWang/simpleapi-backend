package com.wsy.simpleapicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsy.simpleapicommon.model.entity.UserInterfaceInfo;


/**
* @author 15790
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-04-21 09:14:38
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 判断是否还有调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCountOver(long interfaceInfoId, long userId);

    /**
     * 判断是否被封禁
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean isBan(long interfaceInfoId, long userId);



}
