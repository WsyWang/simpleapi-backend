package com.wsy.simpleapi.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wsy.simpleapi.common.ErrorCode;
import com.wsy.simpleapi.exception.ThrowUtils;
import com.wsy.simpleapi.mapper.InterfaceInfoMapper;
import com.wsy.simpleapicommon.model.entity.InterfaceInfo;
import com.wsy.simpleapicommon.service.InnerInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        ThrowUtils.throwIf(StrUtil.hasEmpty(path, method), ErrorCode.PARAMS_ERROR);
        QueryWrapper<InterfaceInfo> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("url", path);
        infoQueryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(infoQueryWrapper);
    }

    @Override
    public List<InterfaceInfo> getInterfaceInfoList() {
        return interfaceInfoMapper.selectList(Wrappers.emptyWrapper());
    }
}
