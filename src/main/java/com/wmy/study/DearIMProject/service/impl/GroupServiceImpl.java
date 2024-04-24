package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.dao.IGroupDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.Group;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.service.IGroupMessageService;
import com.wmy.study.DearIMProject.service.IGroupService;
import com.wmy.study.DearIMProject.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<IGroupDao, Group> implements IGroupService {

    @Resource
    private IUserService userService;

    /**
     * 创建群聊
     * 该函数用于创建一个群组，
     * 将传入的用户列表中的用户ID保存到群组中，
     * 并将该群组保存到数据库。
     * 如果保存成功，则返回创建的群组对象，否则返回null。
     *
     * @param aUserIds 群聊的用户列表
     * @param token    用户的token
     * @return 群聊对象
     */
    @Override
    @Transactional
    public Group createGroup(List<Long> aUserIds, String token) throws BusinessException {
        List<User> users = hasPermissionToAddUserIds(aUserIds, token);
        if (users == null || users.isEmpty()) {
            throw new BusinessException(ErrorCode.ERROR_CODE_NO_PERMISSION, "error.no_permission");
        }
        User createUser = userService.getFromToken(token);
        if (createUser == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_not_found");
        }
        List<Long> userIds = new ArrayList<>();
        for (User user : users) {
            userIds.add(user.getUserId());

        }
        Group group = new Group();
        group.setContentUserIds(userIds);
        group.setOwnUserId(createUser.getUserId());
        group.setName("群聊");
        group.setMUserIds(String.valueOf(createUser.getUserId()));
        boolean save = save(group);
        if (save) {
            log.debug("创建群组成功 group = {}", group);
            // 给每个用户添加groupId
            for (User user : users) {
                List<Long> contentGroupIds = user.getContentGroupIds();
                contentGroupIds.add(group.getGroupId());
                userService.updateById(user);
            }
            return group;
        }
        return null;
    }

    /**
     * 判断用户是否有权限添加用户
     * 该函数通过给定的token获取用户信息，然后判断传入的userIds是否有重复，并通过查询数据库判断userIds中的用户是否存在。最后返回存在数据库中的用户列表。
     *
     * @param aUserIds 添加的用户ids
     * @param token    用户token
     * @return 用户的user列表
     */
    private List<User> hasPermissionToAddUserIds(List<Long> aUserIds, String token) {
        User user = userService.getFromToken(token);
        if (user == null) {
            return null;
        }
        List<Long> userIds = new ArrayList<>(aUserIds);
        userIds.add(user.getUserId());
        // 判断userId是否有重复
        HashSet<Long> hashSetUserIds = new HashSet<>(userIds);
        // 判断userId是否存在
        LambdaQueryWrapper<User> query = Wrappers.<User>lambdaQuery();
        query.in(User::getUserId, hashSetUserIds);
        return userService.list(query);
    }

    /**
     * 添加用户到群组
     *
     * @param groupId 群组id
     * @param userIds 添加的用户id
     * @param token   用户token
     * @return 是否添加成功
     */
    @Override
    @Transactional
    public boolean addUserToGroup(Long groupId, List<Long> userIds, String token) throws BusinessException, JsonProcessingException {
        // 判断用户是否存在
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        Group group = getById(groupId);
        // 判断群组是否存在
        if (group == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
        }
        // 判断是否有添加用户权限
        boolean hasPermissionToEdit = group.hasPermissionToEdit(user.getUserId());
        if (!hasPermissionToEdit) {
            throw new BusinessException(ErrorCode.ERROR_CODE_NO_PERMISSION, "error.no_permission");
        }
        // 判断用户是否已经加入群组
        if (new HashSet<>(group.getContentUserIds()).containsAll(userIds)) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_HAS_EXIST_USER, "error.group_has_exist_user");
        }
        HashSet<Long> userIdSet = new HashSet<>(userIds);
        ArrayList<Long> contentUserIds = new ArrayList<>(group.getContentUserIds());
        for (Long userId : userIdSet) {
            if (!contentUserIds.contains(userId)) {
                contentUserIds.add(userId);
            }
        }
        List<User> users = userService.listByIds(userIds);
        for (User aUser : users) {
            aUser.getContentGroupIds().add(groupId);
            userService.updateById(aUser);
        }

        group.setContentUserIds(contentUserIds);
        UpdateWrapper<Group> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("user_ids", group.getUserIds());
        updateWrapper.eq("group_id", group.getGroupId());
        return update(updateWrapper);
    }

    @Override
    public boolean updateGroupName(Long groupId, String groupName, String token) throws BusinessException {
        // 判断用户是否存在
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        // 判断群组是否存在
        Group group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
        }
        // 判断是否有修改群组权限
        boolean hasPermissionToEdit = group.hasPermissionToEdit(user.getUserId());
        if (!hasPermissionToEdit) {
            throw new BusinessException(ErrorCode.ERROR_CODE_NO_PERMISSION, "error.no_permission");
        }
        // 修改群组名称
        group.setName(groupName);
        UpdateWrapper<Group> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("name", group.getName());
        updateWrapper.eq("group_id", group.getGroupId());
        // 返回修改结果
        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean deleteGroup(Long groupId, String token) throws BusinessException {
        // 判断用户是否存在
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        // 判断群组是否存在
        Group group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
        }
        // 判断用户是否有删除群组的权限
        boolean hasPermissionToEdit = group.hasPermissionToEdit(user.getUserId());
        if (!hasPermissionToEdit) {
            throw new BusinessException(ErrorCode.ERROR_CODE_NO_PERMISSION, "error.no_permission");
        }


        List<Long> userIds = group.getContentUserIds();
        List<User> users = userService.listByIds(userIds);
        for (User aUser : users) {
            aUser.getContentGroupIds().remove(groupId);
            userService.updateById(aUser);

        }
        // 删除群组
        // 返回删除结果
        return removeById(groupId);
    }

    @Override
    public boolean deleteUserFromGroup(Long groupId, Long userId, String token) throws BusinessException {
        // 判断用户是否存在
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        // 判断群组是否存在
        Group group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
        }
        // 判断用户是否有删除群组的权限
        boolean hasPermissionToEdit = group.hasPermissionToEdit(user.getUserId());
        if (!hasPermissionToEdit) {
            throw new BusinessException(ErrorCode.ERROR_CODE_NO_PERMISSION, "error.no_permission");
        }
        // 删除群组中的用户
        if (group.getContentUserIds().contains(userId)) {
            ArrayList<Long> contentUserIds = new ArrayList<>(group.getContentUserIds());
            contentUserIds.remove(userId);
            group.setContentUserIds(contentUserIds);
            UpdateWrapper<Group> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("user_ids", group.getUserIds());
            updateWrapper.eq("group_id", group.getGroupId());
            return update(updateWrapper);
        }
        return false;
    }

    @Override
    public Group getGroupInfo(Long groupId, String token) throws BusinessException {
        // 判断用户是否存在
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        // 判断群组是否存在
        Group group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_GROUP_NOT_EXIST, "error.group_has_not_exist");
        }
        if (group.getContentUserIds().contains(user.getUserId())) {
            return group;
        }
        return null;
    }

    @Override
    public List<Group> getGroupList(String token) throws BusinessException {
        User user = userService.getFromToken(token);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "error.user_has_not_exist");
        }
        // userIds 包含userId的群组
        QueryWrapper<Group> wrapper = new QueryWrapper<>();
        wrapper.like("user_ids", user.getUserId().toString() + ",").or().like("user_ids", "," + user.getUserId().toString());
        List<Group> list = list(wrapper);
        log.debug(list.toString());
        return list;
    }


}
