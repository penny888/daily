package com.daily.config;

import com.alibaba.fastjson.JSON;
import com.daily.dao.jpa.UserRepository;
import com.daily.dto.Response;
import com.daily.entity.DailyUser;
import com.daily.utils.EncryptUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        Response response1 = new Response();
        HttpSession session = request.getSession();
        System.out.println("当前请求session-id: " + session.getId());
        String userToken = request.getHeader("userToken");
        System.out.println("当前请求head头userToken: " + userToken);
        String uri = request.getRequestURI();
        System.out.println("当前请求uri: " + uri);
        if (uri.contains("api/")) {
            DailyUser sessionUser = (DailyUser) session.getAttribute("user");
            if (Objects.isNull(sessionUser)) {
                System.out.println("当前session为空");
                if (Objects.isNull(userToken) || Strings.isEmpty(userToken) || Strings.isBlank(userToken)) {
                    System.out.println("head头userToken为空");
                    response1.setSuccess(false);
                    response1.setMsg("员工未登录");
                    response.getWriter().write(JSON.toJSONString(response1));
                    return false;
                } else {
                    System.out.println("head头userToken有值");
                    String DecryptedToken = EncryptUtil.decrypt(userToken);
                    System.out.println("解密后userToken：" + DecryptedToken);
                    String[] stringArr = DecryptedToken.split("\\.");
                    long currentTimeStamp = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
                    if (currentTimeStamp - Long.parseLong(stringArr[1]) > 604800) {
                        // userToken过期时间7天
                        response1.setSuccess(false);
                        response1.setMsg("员工token已过期，请重新登录");
                        response.getWriter().write(JSON.toJSONString(response1));
                        return false;
                    }
                    DailyUser user1 = JSON.parseObject(stringArr[0], DailyUser.class);
                    Optional<DailyUser> optional = userRepository.findByAccount(Objects.requireNonNull(user1).getAccount());
                    if (optional.isPresent()) {
                        DailyUser user2 = optional.get();
                        if (Objects.equals(user2.getPassword(), user1.getPassword())) {
                            request.getSession().setAttribute("user", user1);
                            return true;
                        } else {
                            response1.setSuccess(false);
                            response1.setMsg("密码错误");
                            response.getWriter().write(JSON.toJSONString(response1));
                            return false;
                        }
                    } else {
                        response1.setSuccess(false);
                        response1.setMsg("该账户不存在");
                        response.getWriter().write(JSON.toJSONString(response1));
                        return false;
                    }
                }
            } else {
                System.out.println("当前session有值");
                return true;
            }
        } else {
            return true;
            // response1.setSuccess(false);
            // response1.setMsg("请求网址非法");
            // response.getWriter().write(JSON.toJSONString(response1));
            // return false;
        }
    }
}
