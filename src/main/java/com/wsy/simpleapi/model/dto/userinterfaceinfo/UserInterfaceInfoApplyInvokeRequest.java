package com.wsy.simpleapi.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 申请测试
 *
 * @author wsy
 */
@Data
public class UserInterfaceInfoApplyInvokeRequest implements Serializable {

    /**
     * 调用接口id
     */
    private Long interfaceInfoId;

    private static final long serialVersionUID = 1L;
}