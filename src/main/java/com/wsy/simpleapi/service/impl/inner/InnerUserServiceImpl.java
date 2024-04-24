package com.wsy.simpleapi.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsy.simpleapi.common.ErrorCode;
import com.wsy.simpleapi.exception.ThrowUtils;
import com.wsy.simpleapi.mapper.UserMapper;
import com.wsy.simpleapicommon.model.entity.User;
import com.wsy.simpleapicommon.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        ThrowUtils.throwIf(StrUtil.isEmpty(accessKey), ErrorCode.PARAMS_ERROR, "用户标识为空");
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(userQueryWrapper);
    }
}
