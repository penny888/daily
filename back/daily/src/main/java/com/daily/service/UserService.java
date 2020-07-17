package com.daily.service;

import com.daily.dto.Response;
import com.daily.dto.UserDto;
import com.daily.entity.DailyUser;
import org.springframework.data.domain.Page;

import java.util.List;


public interface UserService {

    Page<DailyUser> listUser(int pageNum, int pageSize);

    com.github.pagehelper.Page<DailyUser> searchUser(UserDto userDto);

    DailyUser getUser(int id);

    Response<DailyUser> login(String account, String password);

    boolean insertUser(List<DailyUser> user);

    boolean updateUser(DailyUser user);

    boolean deleteUser(int id);
}
