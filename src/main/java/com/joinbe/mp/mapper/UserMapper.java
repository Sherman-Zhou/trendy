package com.joinbe.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joinbe.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByLogin(String login);

}
