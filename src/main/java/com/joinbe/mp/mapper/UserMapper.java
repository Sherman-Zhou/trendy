package com.joinbe.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joinbe.domain.Staff;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<Staff> {

    Staff selectByLogin(String login);

}
