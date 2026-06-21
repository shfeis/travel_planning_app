package com.yexiang.travelPlanning.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 返回给前端的用户信息，不包含密码
 */
@Data
@AllArgsConstructor
public class UserProfile {
    private String id;
    private String name;
    private String logo;
}
