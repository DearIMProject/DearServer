package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.Group;

import java.util.List;


public interface IGroupService extends IService<Group> {
    /**
     * 创建群组
     *
     * @param userIds 用户id数组
     * @param token   用户token
     * @return 群组
     * @throws BusinessException 中途判定
     */
    Group createGroup(List<Long> userIds, String token) throws BusinessException;

    /**
     * 往群组中添加用户
     *
     * @param groupId 群组id
     * @param userIds 用户id数组
     * @param token   用户token
     * @return 是否成功添加
     * @throws BusinessException 中途判定
     */
    boolean addUserToGroup(Long groupId, List<Long> userIds, String token) throws BusinessException, JsonProcessingException;

    /**
     * 更新群组名称
     *
     * @param groupId   群组id
     * @param groupName 群组名称
     * @param token     用户token
     * @return 是否成功更新
     * @throws BusinessException 中途判定错误
     */
    boolean updateGroupName(Long groupId, String groupName, String token) throws BusinessException;

    /**
     * 删除群组
     *
     * @param groupId 群组id
     * @param token   用户token
     * @return 是否成功删除
     */
    boolean deleteGroup(Long groupId, String token) throws BusinessException;

    /**
     * 删除群组中的用户
     *
     * @param groupId 群组id
     * @param userId  用户id
     * @param token   用户token
     * @return 是否成功删除
     */
    boolean deleteUserFromGroup(Long groupId, Long userId, String token) throws BusinessException;

    /**
     * 获取群组信息
     *
     * @param groupId 群组id
     * @param token   用户token
     * @return 群组信息
     */
    Group getGroupInfo(Long groupId, String token) throws BusinessException;

    /**
     * 获取群列表
     *
     * @param token 用户token
     * @return 群列表
     */
    List<Group> getGroupList(String token) throws BusinessException;
}
