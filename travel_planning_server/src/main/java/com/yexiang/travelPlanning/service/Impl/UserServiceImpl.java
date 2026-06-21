package com.yexiang.travelPlanning.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yexiang.travelPlanning.Entity.User;
import com.yexiang.travelPlanning.mapper.UserMapper;
import com.yexiang.travelPlanning.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
