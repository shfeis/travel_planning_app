package com.yexiang.travelPlanning.Entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表对应实体
 */
@Data
@TableName("`user`")
public class User {
    /**
     * 手机号
     */
    @TableId
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 头像
     */
    private byte[] logo;
}
