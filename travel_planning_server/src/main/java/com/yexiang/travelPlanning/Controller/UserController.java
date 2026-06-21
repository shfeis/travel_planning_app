package com.yexiang.travelPlanning.Controller;

import com.yexiang.travelPlanning.Entity.User;
import com.yexiang.travelPlanning.Entity.UserAuthRequest;
import com.yexiang.travelPlanning.Entity.UserProfile;
import com.yexiang.travelPlanning.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

/**
 * 用户登录和注册
 */
@RestController
@RequestMapping("user")
public class UserController {
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody(required = false) UserAuthRequest request) {
        String id = normalize(request == null ? null : request.getId());
        String name = normalize(request == null ? null : request.getName());
        String pwd = normalize(request == null ? null : request.getPwd());

        ResponseEntity<?> validationError = validateAuthFields(id, pwd);
        if (validationError != null) {
            return validationError;
        }

        if (!StringUtils.hasText(name)) {
            return ResponseEntity.badRequest().body(error("请输入用户名"));
        }

        if (name.length() < 2 || name.length() > 20) {
            return ResponseEntity.badRequest().body(error("用户名长度应为2-20个字符"));
        }

        if (userService.getById(id) != null) {
            return ResponseEntity.status(409).body(error("该手机号已注册"));
        }

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPwd(pwd);
        userService.save(user);

        return ResponseEntity.ok(toProfile(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) UserAuthRequest request) {
        String id = normalize(request == null ? null : request.getId());
        String pwd = normalize(request == null ? null : request.getPwd());

        ResponseEntity<?> validationError = validateAuthFields(id, pwd);
        if (validationError != null) {
            return validationError;
        }

        User user = userService.getById(id);
        if (user == null || !pwd.equals(user.getPwd())) {
            return ResponseEntity.status(401).body(error("手机号或密码错误"));
        }

        return ResponseEntity.ok(toProfile(user));
    }

    private ResponseEntity<?> validateAuthFields(String id, String pwd) {
        if (!StringUtils.hasText(id)) {
            return ResponseEntity.badRequest().body(error("请输入手机号"));
        }

        if (!id.matches(PHONE_PATTERN)) {
            return ResponseEntity.badRequest().body(error("请输入正确的手机号"));
        }

        if (!StringUtils.hasText(pwd)) {
            return ResponseEntity.badRequest().body(error("请输入密码"));
        }

        if (pwd.length() < 6 || pwd.length() > 50) {
            return ResponseEntity.badRequest().body(error("密码长度应为6-50位"));
        }

        return null;
    }

    private UserProfile toProfile(User user) {
        String logo = null;
        if (user.getLogo() != null && user.getLogo().length > 0) {
            logo = Base64.getEncoder().encodeToString(user.getLogo());
        }

        return new UserProfile(user.getId(), user.getName(), logo);
    }

    private Map<String, String> error(String message) {
        return Map.of("message", message);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
