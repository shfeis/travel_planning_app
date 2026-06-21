package com.yexiang.travelPlanning.Entity;

import lombok.Data;

/**
 * 接收用户前端传入的目的地、预算、天数
 */
@Data
public class Recommend {
    private String city;
    private String budget;
    private String days;
}
