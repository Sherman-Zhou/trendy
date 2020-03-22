package com.joinbe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joinbe.domain.User;


public interface UserMapper extends BaseMapper<User> {

    User selectByLogin(String login);

}
