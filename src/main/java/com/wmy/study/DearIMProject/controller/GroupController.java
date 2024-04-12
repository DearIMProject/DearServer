package com.wmy.study.DearIMProject.controller;

import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.Group;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.service.IGroupMessageService;
import com.wmy.study.DearIMProject.service.IGroupService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {

    @Resource
    private IGroupService groupService;

    @Resource
    private IGroupMessageService groupMessageService;


    /**
     * 创建群组
     *
     * @param userIds 用户id数组
     * @param token   用户token
     * @return 成功 or 失败
     */
    @PostMapping("/create")
    public ResponseBean createGroup(String userIds, String token) throws BusinessException {
        String[] numberStrings = userIds.trim().split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();

        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            longNumbers.add(Long.parseLong(numberString));
        }
        List<Long> aUserIds = longNumbers;
        // 判断入参合法性
        if (aUserIds.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_EMPTY_PARAM, "error.empty_param");
        }

        Map<String, Object> map = new HashMap<>();

        Group group = groupService.createGroup(aUserIds, token);
        if (group != null) {
            map.put("group", group);
            try {
                groupMessageService.sendAddGroupMessage(aUserIds, group.getGroupId());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new ResponseBean(true, map);
        }


        return new ResponseBean(false, ErrorCode.ERROR_CODE_CREATE_FAILURE, "error.create_failure");
    }

    /**
     * 添加用户到群组
     *
     * @param groupId  群组id
     * @param aUserIds 用户id数组
     * @param token    用户token
     * @return 成功 or 失败
     */
    @PostMapping("/addUsers")
    public ResponseBean addUsersToGroup(Long groupId, List<Long> aUserIds, String token) throws BusinessException, JsonProcessingException {
        // 判断参数合法性
        if (aUserIds == null || aUserIds.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_EMPTY_PARAM, "error.empty_param");
        }

        boolean success = groupService.addUserToGroup(groupId, aUserIds, token);
        if (success) {
            try {
                groupMessageService.sendAddGroupMessage(aUserIds, groupId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_ADD_FAILURE, "error.add_failure");
    }

    /**
     * 更新群组名称
     *
     * @param groupId   群组id
     * @param groupName 群组名称
     * @param token     用户token
     * @return 成功 or 失败
     */
    @PostMapping("/updateGroupName")
    public ResponseBean updateGroupName(Long groupId, String groupName, String token) throws BusinessException {
        if (groupName.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_EMPTY_PARAM, "error.empty_param");
        }
        boolean success = groupService.updateGroupName(groupId, groupName, token);
        if (success) {
            try {
                groupMessageService.sendGroupUpdateMessage(groupId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_ADD_FAILURE, "error.add_failure");
    }

    /**
     * 删除群组
     *
     * @param groupId 群组id
     * @param token   用户token
     * @return 成功 or 失败
     */
    @PostMapping("/delete")
    public ResponseBean deleteGroup(Long groupId, String token) throws BusinessException {
        Group group = groupService.getById(groupId);
        boolean success = groupService.deleteGroup(groupId, token);
        if (success) {
            try {
                groupMessageService.sendGroupDeleteMessage(group);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_DELETE_FAILURE, "error.delete_failure");
    }

    /**
     * 删除群组中的用户
     *
     * @param groupId 群组id
     * @param userId  用户id
     * @param token   用户token
     * @return 成功 or 失败
     */
    @PostMapping("/deleteUser")
    public ResponseBean deleteUserFromGroup(Long groupId, Long userId, String token) throws BusinessException, JsonProcessingException {
        boolean success = groupService.deleteUserFromGroup(groupId, userId, token);
        if (success) {
            groupMessageService.sendGroupDeleteMessage(userId, groupId);
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_DELETE_FAILURE, "error.delete_failure");
    }

    @PostMapping("/info")
    public ResponseBean getGroup(Long groupId, String token) throws BusinessException {
        Group group = groupService.getGroupInfo(groupId, token);
        if (group != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("group", group);
            return new ResponseBean(true, map);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
    }

    @PostMapping("/list")
    public ResponseBean getGroupList(String token) throws BusinessException {
        List<Group> groupList = groupService.getGroupList(token);
        if (groupList != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("list", groupList);
            return new ResponseBean(true, map);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
    }
}
