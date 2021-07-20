package com.wang.ShardingSphere;


import com.wang.model.User1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Classname UserController
 * @Description 用户测试控制类
 * @Author 章国文 13120739083@163.com
 * @Date 2019-06-28 17:36
 * @Version 1.0
 */
@RestController
public class UsersharedController {

    @Autowired
    private UserService userService;

    @GetMapping("/select")
    public List<User1> select() {
        return userService.getUserList();
    }

    @GetMapping("/insert")
    public Boolean insert(User1 user) {
        return userService.save(user);
    }

}