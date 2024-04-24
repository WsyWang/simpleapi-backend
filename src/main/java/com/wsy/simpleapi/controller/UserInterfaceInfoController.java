package com.wsy.simpleapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsy.simpleapi.annotation.AuthCheck;
import com.wsy.simpleapi.common.*;
import com.wsy.simpleapi.constant.UserConstant;
import com.wsy.simpleapi.exception.BusinessException;
import com.wsy.simpleapi.exception.ThrowUtils;
import com.wsy.simpleapi.model.commonenum.UserInterfaceInfoStatusEnum;
import com.wsy.simpleapi.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.wsy.simpleapi.model.dto.userinterfaceinfo.UserInterfaceInfoApplyInvokeRequest;
import com.wsy.simpleapi.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.wsy.simpleapi.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.wsy.simpleapi.service.UserInterfaceInfoService;
import com.wsy.simpleapi.service.UserService;
import com.wsy.simpleapicommon.model.entity.User;
import com.wsy.simpleapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 接口额度管理
 *
 * @author wsy
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     * 添加的逻辑：判断请求是否为空 => 调用service层的校验方法 => 获取当前用户id => 保存数据 => 返回新创建的id
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求体 里面只有id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 申请调用次数
     *
     * @param userInterfaceInfoApplyInvokeRequest
     * @return
     */
    @PostMapping("/testCount")
    public BaseResponse<Boolean> applyInvokeCount(@RequestBody UserInterfaceInfoApplyInvokeRequest userInterfaceInfoApplyInvokeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userInterfaceInfoApplyInvokeRequest == null || request == null, ErrorCode.PARAMS_ERROR);
        Long interfaceInfoId = userInterfaceInfoApplyInvokeRequest.getInterfaceInfoId();
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserInterfaceInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", userId);
        wrapper.eq("interfaceInfoId", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(wrapper);
        if (userInterfaceInfo == null) {
            UserInterfaceInfo userInterfaceInfoAdd = new UserInterfaceInfo();
            userInterfaceInfoAdd.setUserId(userId);
            userInterfaceInfoAdd.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfoAdd.setLeftNum(10);
            boolean save = userInterfaceInfoService.save(userInterfaceInfoAdd);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
        }
        ThrowUtils.throwIf(userInterfaceInfo.getStatus() == UserInterfaceInfoStatusEnum.USER_INTERFACE_INFO_STATUS_BAN.getValue(), ErrorCode.NO_AUTH_ERROR);
        UserInterfaceInfo userInterfaceInfo1 = new UserInterfaceInfo();
        userInterfaceInfo1.setId(userInterfaceInfo.getId());
        userInterfaceInfo1.setLeftNum(userInterfaceInfo.getLeftNum() + 10);
        boolean update = userInterfaceInfoService.updateById(userInterfaceInfo1);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoPage);
    }

    // endregion 解禁封禁

    /**
     * 封禁
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/ban")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> ban(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Long userInterfaceId = idRequest.getId();
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(userInterfaceId);
        ThrowUtils.throwIf(userInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        UserInterfaceInfo newUserInterfaceInfo = new UserInterfaceInfo();
        newUserInterfaceInfo.setId(userInterfaceId);
        newUserInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.USER_INTERFACE_INFO_STATUS_BAN.getValue());
        boolean result = userInterfaceInfoService.updateById(newUserInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 解禁
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/notBan")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> notBan(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Long userInterfaceId = idRequest.getId();
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(userInterfaceId);
        ThrowUtils.throwIf(userInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        UserInterfaceInfo newUserInterfaceInfo = new UserInterfaceInfo();
        newUserInterfaceInfo.setId(userInterfaceId);
        newUserInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.USER_INTERFACE_INFO_STATUS_NORMAL.getValue());
        boolean result = userInterfaceInfoService.updateById(newUserInterfaceInfo);
        return ResultUtils.success(result);
    }


}
