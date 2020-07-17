package com.daily.dao.mybatis;

import com.daily.dto.UserDto;
import com.daily.entity.DailyUser;
import com.github.pagehelper.Page;

public interface UserMapper {

    Page<DailyUser> searchUser(UserDto userDto);

}
