package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class FriendController {

    // 模拟数据库：保存 token -> userId
    private static final Map<String, String> tokenMap = new ConcurrentHashMap<>();

    private static final String BASE_URL = "http://10.0.2.2:8080/addFriend";

    /**
     * 获取加好友二维码链接
     */
    @GetMapping("/api/friend/getLink")
    public Map<String, String> getFriendLink(
            @RequestParam(required = false, defaultValue = "testUser") String userId) {

        // 生成唯一 token
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, userId);

        Map<String, String> map = new HashMap<>();
        map.put("friendId", BASE_URL + "?token=" + token);

        System.out.println("FriendController called, userId=" + userId + ", token=" + token);
        return map;
    }

    /**
     * 扫描二维码后访问这个接口，实现加好友逻辑
     */
    @GetMapping("/addFriend")
    public String addFriend(@RequestParam String token) {
        String userId = tokenMap.get(token);
        if (userId == null) {
            return "Invalid or expired QR code!";
        }

        // TODO: 在这里实现真正的加好友逻辑，比如保存好友关系到数据库
        return "Friend request sent to user: " + userId;
    }
}
