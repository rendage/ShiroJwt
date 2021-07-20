package com.wang.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.UserMapper;
import com.wang.model.User1;
import com.wang.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname UserServiceImpl
 * @Description 用户服务实现类
 * @Author 章国文 13120739083@163.com
 * @Date 2019-06-28 17:32
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User1> implements UserService {
    @Override
    public boolean save(User1 entity) {
        return super.save(entity);
    }

    @Override
    public List<User1> getUserList() {
        return baseMapper.selectList(Wrappers.<User1>lambdaQuery());
    }

}