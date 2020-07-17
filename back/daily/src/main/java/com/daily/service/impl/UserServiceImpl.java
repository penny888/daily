package com.daily.service.impl;

import com.alibaba.fastjson.JSON;
import com.daily.dao.jpa.UserRepository;
import com.daily.dao.mybatis.UserMapper;
import com.daily.dto.Response;
import com.daily.dto.UserDto;
import com.daily.entity.DailyUser;
import com.daily.service.UserService;
import com.daily.utils.EncryptUtil;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    // 密码前缀
    private static final String PASSWORD_PREFIX = "daily2020";

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<DailyUser> listUser(int pageNum, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        return userRepository.findAll((Specification<DailyUser>) (user, q, cb) -> cb.equal(user.get("isAdmin"), "0") , pageable);
    }

    @Override
    public com.github.pagehelper.Page<DailyUser> searchUser(UserDto userDto) {
        PageHelper.startPage(userDto.getPageNum(), userDto.getPageSize());
        PageHelper.orderBy("id DESC");
        return userMapper.searchUser(userDto);
    }

    @Override
    public DailyUser getUser(int id) {
        Optional<DailyUser> optional = userRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Response<DailyUser> login(String account, String password) {
        Response<DailyUser> response = new Response<>();
        Optional<DailyUser> optional = userRepository.findByAccount(account);
        if (optional.isPresent()) {
            DailyUser entity = optional.get();
            if (Objects.equals(encryptPassword(password), entity.getPassword())) {
                response.setSuccess(true);
                entity.setDeleteFlag(null);
                String userToken = null;
                try {
                    userToken = EncryptUtil.encrypt(JSON.toJSONString(entity) + '.' + String.valueOf(System.currentTimeMillis()).substring(0, 10));
                } catch(Exception e) {
                    log.info("The test info is :{}", e.toString());
                    response.setSuccess(false);
                    response.setMsg("系统异常，请重新登录");
                }
                response.setData(entity);
                response.setUserToken(userToken);
            } else {
                response.setSuccess(false);
                response.setMsg("密码错误");
            }
        } else {
            response.setSuccess(false);
            response.setMsg("该账户不存在");
        }
        return response;
    }

    @Override
    public boolean insertUser(List<DailyUser> userList) {
        for (DailyUser user: userList) {
            user.setPassword(encryptPassword(user.getPassword()));
        }
        userRepository.saveAll(userList);
        return true;
    }

    @Override
    public boolean updateUser(DailyUser user) {
        Optional<DailyUser> optional = userRepository.findById(user.getId());
        if (optional.isPresent()) {
            DailyUser entity = optional.get();
            if (Objects.nonNull(user.getAccount())) {
                entity.setAccount(user.getAccount());
            }
            if (Objects.nonNull(user.getName())) {
                entity.setName(user.getName());
            }
            if ( Objects.nonNull(user.getDepartment())) {
                entity.setDepartment(user.getDepartment());
            }
            if (Objects.nonNull(user.getPassword()) && Strings.isNotEmpty(user.getPassword())) {
                entity.setPassword(encryptPassword(user.getPassword()));
            }
            userRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(int id) {
        Optional<DailyUser> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            DailyUser entity = optional.get();
            entity.setDeleteFlag("1");
            userRepository.save(entity);
            return true;
        }
        return false;
    }

    // 加密密码
    private String encryptPassword(String password) {
        try {
            String str = EncryptUtil.getMDString(PASSWORD_PREFIX + password, "SHA-256");
            return EncryptUtil.getMDString(PASSWORD_PREFIX + str, "MD5");
        } catch (Exception e) {
            log.info("The test info is :{}", e.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String str = EncryptUtil.getMDString(PASSWORD_PREFIX + "123456", "SHA-256");
        System.out.println(EncryptUtil.getMDString(PASSWORD_PREFIX + str, "MD5"));
    }

}
