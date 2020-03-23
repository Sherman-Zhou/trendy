package com.joinbe.service.impl.mp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joinbe.domain.UserRole;
import com.joinbe.mapper.UserRoleMapper;
import com.joinbe.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class MpUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
