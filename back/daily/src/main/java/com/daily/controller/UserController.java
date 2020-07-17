package com.daily.controller;

import com.daily.dao.jpa.UserRepository;
import com.daily.dto.Response;
import com.daily.dto.UserDto;
import com.daily.entity.DailyUser;
import com.daily.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Api(value = "员工管理接口", tags = "员工管理接口", description = "员工管理接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private HttpSession httpSession;

    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @GetMapping("/list-user")
    @ApiOperation(value = "员工列表", notes = "员工列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "pageNum", example = "1", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "pageSize", example = "10", required = true, dataType = "int", paramType = "query")
    })
    public Response<List<DailyUser>> listUser(Integer pageNum, Integer pageSize) {
        Response<List<DailyUser>> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            }
            if (Objects.isNull(pageNum) || Objects.isNull(pageSize)) {
                response.setSuccess(false);
                response.setMsg("pageNum或pageSize不能为空");
                return response;
            }
            Page<DailyUser> page = userService.listUser(pageNum, pageSize);
            List<DailyUser> list = page.getContent();
            for (DailyUser user : list) {
                user.setPassword(null);
                user.setDeleteFlag(null);
            }
            response.setSuccess(true);
            response.setData(list);
            response.setTotal(page.getTotalElements());
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @PostMapping("/search-user")
    @ApiOperation(value = "查询员工", notes = "查询员工")
    public Response<List<DailyUser>> searchUser(@RequestBody UserDto userDto) {
        Response<List<DailyUser>> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            }
            if (Objects.isNull(userDto.getPageNum()) || Objects.isNull(userDto.getPageSize())) {
                response.setSuccess(false);
                response.setMsg("pageNum或pageSize不能为空");
                return response;
            }
            com.github.pagehelper.Page<DailyUser> pageUser = userService.searchUser(userDto);
            response.setSuccess(true);
            response.setData(pageUser.getResult());
            response.setTotal(pageUser.getTotal());
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @GetMapping("/get-user")
    @ApiOperation(value = "员工详情", notes = "员工详情")
    @ApiImplicitParam(name = "id", value = "员工id", example = "1", required = true, dataType = "int", paramType = "query")
    public Response<DailyUser> getUser(Integer id) {
        Response<DailyUser> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            System.out.println("isAdmin=" + isAdmin);
            if (!Objects.equals(isAdmin, "1")) {
                id = sessionUser.getId();
            } else {
                if (Objects.isNull(id)) {
                    id = sessionUser.getId();
                }
            }
            DailyUser user = userService.getUser(id);
            if (Objects.nonNull(user)) {
                if (!Objects.equals(isAdmin, "1")) {
                    user.setIsAdmin(null);
                } else {
                    if (!Objects.equals(user.getIsAdmin(), "1")) {
                        user.setIsAdmin(null);
                    }
                }
                user.setPassword(null);
                user.setDeleteFlag(null);
                response.setSuccess(true);
                response.setData(user);
            } else {
                response.setSuccess(false);
                response.setMsg("该账户不存在");
            }
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @PostMapping("/login")
    @ApiOperation(value = "员工登录", notes = "员工登录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "账户", example = "tony", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "password", value = "密码", example = "111111", required = true, dataType = "String", paramType = "query")
    })
    public Response<DailyUser> login(String account, String password) {
        Response<DailyUser> response = new Response<>();
        try {
            if (Objects.isNull(account) || Strings.isEmpty(account)) {
                response.setSuccess(false);
                response.setMsg("账户不能为空");
                return response;
            }
            if (Objects.isNull(password) || Strings.isEmpty(password)) {
                response.setSuccess(false);
                response.setMsg("密码不能为空");
                return response;
            }
            response = userService.login(account, password);
            if (response.isSuccess()) {
                httpSession.setAttribute("user", response.getData());
                DailyUser user = (DailyUser) response.getData().clone();
                user.setPassword(null);
                user.setIsAdmin(null);
                response.setData(user);
            } else {
                response.setData(null);
            }
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @PostMapping("/insert-user")
    @ApiOperation(value = "新增员工", notes = "新增员工")
    public Response insertUser(@RequestBody List<DailyUser> userList) {
        Response response = new Response();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            }
            for (DailyUser user : userList) {
                String account = user.getAccount();
                if (Objects.isNull(account) || Strings.isEmpty(account) || Strings.isBlank(account)) {
                    response.setSuccess(false);
                    response.setMsg("账户不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[a-zA-Z0-9]{4,10}$", account)) {
                        response.setSuccess(false);
                        response.setMsg("账户必须为4到10位数字或英文字母");
                        return response;
                    }
                    Optional<DailyUser> optional = userRepository.findByAccount(user.getAccount());
                    if (optional.isPresent()) {
                        response.setSuccess(false);
                        response.setMsg("该账户已存在");
                        return response;
                    }
                }
                String name = user.getName();
                if (Objects.isNull(name) || Strings.isEmpty(name) || Strings.isBlank(name)) {
                    response.setSuccess(false);
                    response.setMsg("姓名不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[\\u4e00-\\u9fa5]{2,10}$", name)) {
                        response.setSuccess(false);
                        response.setMsg("姓名必须为2到10位汉字");
                        return response;
                    }
                }
                String password = user.getPassword();
                if (Objects.isNull(password) || Strings.isEmpty(password) || Strings.isBlank(password)) {
                    response.setSuccess(false);
                    response.setMsg("密码不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[a-zA-Z0-9]{6,12}$", password)) {
                        response.setSuccess(false);
                        response.setMsg("密码必须为6到12位数字或英文字母");
                        return response;
                    }
                }
                String department = user.getDepartment();
                if (Objects.isNull(department) || Strings.isEmpty(department) || Strings.isBlank(department)) {
                    response.setSuccess(false);
                    response.setMsg("部门不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[\\u4e00-\\u9fa5]{2,10}$", department)) {
                        response.setSuccess(false);
                        response.setMsg("部门必须为2到10位汉字");
                        return response;
                    }
                }
                user.setIsAdmin("0");
                user.setDeleteFlag("0");
            }
            boolean result = userService.insertUser(userList);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @PostMapping("/update-user")
    @ApiOperation(value = "更新员工", notes = "更新员工")
    public Response updateUser(@RequestBody DailyUser user) {
        Response response = new Response();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();

            if (Objects.equals(isAdmin, "1")) {
                if (Objects.isNull(user.getId())) {
                    user.setId(sessionUser.getId());
                }
                DailyUser user1 = userService.getUser(user.getId());
                if (Objects.isNull(user1)) {
                    response.setSuccess(false);
                    response.setMsg("该账户不存在");
                    return response;
                }

                String account = user.getAccount();
                if (Objects.isNull(account) || Strings.isEmpty(account) || Strings.isBlank(account)) {
                    response.setSuccess(false);
                    response.setMsg("账户不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[a-zA-Z0-9]{4,10}$", account)) {
                        response.setSuccess(false);
                        response.setMsg("账户必须为4到10位数字或英文字母");
                        return response;
                    }
                }
                String name = user.getName();
                if (Objects.isNull(name) || Strings.isEmpty(name) || Strings.isBlank(name)) {
                    response.setSuccess(false);
                    response.setMsg("姓名不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[\\u4e00-\\u9fa5]{2,10}$", name)) {
                        response.setSuccess(false);
                        response.setMsg("姓名必须为2到10位汉字");
                        return response;
                    }
                }

                String department = user.getDepartment();
                if (Objects.isNull(department) || Strings.isEmpty(department) || Strings.isBlank(department)) {
                    response.setSuccess(false);
                    response.setMsg("部门不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[\\u4e00-\\u9fa5]{2,10}$", department)) {
                        response.setSuccess(false);
                        response.setMsg("部门必须为2到10位汉字");
                        return response;
                    }
                }

                String password = user.getPassword();
                if (Objects.nonNull(password) && Strings.isNotEmpty(password)) {
                    if (!Pattern.matches("^[a-zA-Z0-9]{6,12}$", password)) {
                        response.setSuccess(false);
                        response.setMsg("密码必须为6到12位数字或英文字母");
                        return response;
                    }
                }
            } else {
                user.setId(sessionUser.getId());
                String password = user.getPassword();
                if (Objects.isNull(password) || Strings.isEmpty(password)) {
                    response.setSuccess(false);
                    response.setMsg("密码不能为空");
                    return response;
                }
                if (!Pattern.matches("^[a-zA-Z0-9]{6,12}$", password)) {
                    response.setSuccess(false);
                    response.setMsg("密码必须为6到12位数字或英文字母");
                    return response;
                }
            }
            boolean result = userService.updateUser(user);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @PostMapping("/delete-user")
    @ApiOperation(value = "删除员工", notes = "删除员工")
    @ApiImplicitParam(name = "id", value = "员工id", example = "1", required = true, dataType = "int", paramType = "query")
    public Response deleteUser(Integer id) {
        Response response = new Response();
        try {
            if (Objects.isNull(id)) {
                response.setSuccess(false);
                response.setMsg("员工id不能为空");
                return response;
            }
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            } else {
                DailyUser user = userService.getUser(id);
                if (Objects.isNull(user)) {
                    response.setSuccess(false);
                    response.setMsg("员工不存在");
                    return response;
                }
                if (Objects.equals(user.getIsAdmin(), "1")) {
                    response.setSuccess(false);
                    response.setMsg("管理员无法删除");
                    return response;
                }
            }
            boolean result = userService.deleteUser(id);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    // 处理异常公共方法
    private void processException(Exception e, Response response) {
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        log.info("Exception is: {}", e.toString());
        log.info("File is: {}", stackTraceElement.getFileName());
        log.info("Line is: {}", stackTraceElement.getLineNumber());
        log.info("Method is: {}", stackTraceElement.getMethodName());
        response.setSuccess(false);
        response.setMsg("服务器异常");
    }
}
