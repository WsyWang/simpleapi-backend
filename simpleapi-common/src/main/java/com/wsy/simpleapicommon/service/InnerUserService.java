package com.wsy.simpleapicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsy.simpleapicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author wsy
 */
public interface InnerUserService {

    /**
     * 数据库中查询是否已分配给用户密钥（secretKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
