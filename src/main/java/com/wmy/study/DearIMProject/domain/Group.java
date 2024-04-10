package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@TableName("tb_group")
public class Group {
    @TableLogic
    private Boolean deleted;
    /**
     * 群组id
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;
    /**
     * 群组名称
     */
    private String name;
    /**
     * group中的成员id
     */
    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Long> contentUserIds;

    private String userIds;

    private Long ownUserId;

    private String mUserIds;

    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Long> managerUserIds;

    public List<Long> getManagerUserIds() {
        String[] numberStrings = mUserIds.split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();

        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            longNumbers.add(Long.parseLong(numberString));
        }
        return longNumbers;
    }

    public void setManagerUserIds(List<Long> managerUserIds) {
        String result = managerUserIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        setUserIds(result);
    }

    public List<Long> getContentUserIds() {
        String[] numberStrings = userIds.split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();

        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            longNumbers.add(Long.parseLong(numberString));
        }
        return longNumbers;
    }

    public void setContentUserIds(List<Long> contentUserIds) {
        String result = contentUserIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        setUserIds(result);
    }

    public boolean hasPermissionToEdit(Long userId) {
        return managerUserIds.contains(userId) || ownUserId.equals(userId);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", contentUserIds=" + contentUserIds +
                ", userIds='" + userIds + '\'' +
                ", ownUserId=" + ownUserId +
                ", mUserIds='" + mUserIds + '\'' +
                ", managerUserIds=" + managerUserIds +
                '}';
    }
}
