package com.yexiang.travelPlanning.Entity;

import lombok.Data;

/**
 * 登录/注册请求参数
 */
@Data
public class UserAuthRequest {
    private String id;
    private String name;
    private String pwd;
}
