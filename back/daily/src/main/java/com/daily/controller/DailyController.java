package com.daily.controller;

import com.daily.dto.DailyDto;
import com.daily.dto.Response;
import com.daily.dto.TotalDailyDto;
import com.daily.entity.DailyContent;
import com.daily.entity.DailyUser;
import com.daily.service.DailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Api(value = "日报管理接口", tags = "日报管理接口", description = "日报管理接口")
@RestController
@RequestMapping("/api/daily")
public class DailyController {

    @Resource
    private HttpSession httpSession;

    @Resource
    private DailyService dailyService;

    @GetMapping("/list-daily")
    @ApiOperation(value = "日报列表", notes = "日报列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "员工id", example = "1", dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "pageNum", value = "pageNum", example = "1", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "pageSize", example = "10", required = true, dataType = "String", paramType = "query")
    })
    public Response<List<Map<String, Object>>> listDaily(Integer userId, Integer pageNum, Integer pageSize) {
        Response<List<Map<String, Object>>> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                userId = sessionUser.getId();
            } else {
                if (Objects.isNull(userId)) {
                    userId = sessionUser.getId();
                }
            }
            if (Objects.isNull(pageNum) || Objects.isNull(pageSize)) {
                response.setSuccess(false);
                response.setMsg("pageNum或pageSize不能为空");
                return response;
            }
            Response<List<DailyContent>> result = dailyService.listDaily(userId, pageNum, pageSize);
            processDailyList(result.getData(), response);
            response.setTotal(result.getTotal());
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    @PostMapping("/search-daily")
    @ApiOperation(value = "查询日报", notes = "查询日报")
    public Response<List<Map<String, Object>>> searchDaily(@RequestBody DailyDto contentDto) {
        Response<List<Map<String, Object>>> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                contentDto.setUserId(sessionUser.getId());
            } else {
                if (Objects.isNull(contentDto.getUserId())) {
                    contentDto.setUserId(sessionUser.getId());
                }
            }
            if (Objects.isNull(contentDto.getPageNum()) || Objects.isNull(contentDto.getPageSize())) {
                response.setSuccess(false);
                response.setMsg("pageNum或pageSize不能为空");
                return response;
            }
            Response<List<DailyContent>> result = dailyService.searchDaily(contentDto);
            processDailyList(result.getData(), response);
            response.setTotal(result.getTotal());
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @GetMapping("/get-daily")
    @ApiOperation(value = "日报详情", notes = "日报详情")
    @ApiImplicitParam(name = "id", value = "日报id", example = "1", required = true, dataType = "int", paramType = "query")
    public Response<DailyContent> getDaily(Integer id) {
        Response<DailyContent> response = new Response<>();
        try {
            if (Objects.isNull(id)) {
                response.setSuccess(false);
                response.setMsg("日报任务id不能为空");
                return response;
            }
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            Integer userId = null;
            if (!Objects.equals(isAdmin, "1")) {
                userId = sessionUser.getId();
            }
            DailyContent content = dailyService.getDaily(userId, id);
            if (Objects.nonNull(content)) {
                content.setDeleteFlag(null);
                response.setSuccess(true);
                response.setData(content);
            } else {
                response.setSuccess(false);
                response.setMsg("该日报不存在");
            }
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    @PostMapping("/insert-daily")
    @ApiOperation(value = "新增日报", notes = "新增日报")
    public Response insertDaily(@RequestBody List<DailyDto> dailyList) {
        Response response = new Response();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            for (DailyDto content : dailyList) {
                content.setUserId(sessionUser.getId());
                String product = content.getProduct();
                if (Objects.isNull(product) || Strings.isEmpty(product) || Strings.isBlank(product)) {
                    response.setSuccess(false);
                    response.setMsg("所属产品不能为空或者空白字符串");
                    return response;
                } else {
                    if (!Pattern.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", product)) {
                        response.setSuccess(false);
                        response.setMsg("所属产品必须为2到10位汉字、英文字母、数字");
                        return response;
                    }
                }
                String contentStr = content.getContent();
                if (Objects.isNull(contentStr) || Strings.isEmpty(contentStr) || Strings.isBlank(contentStr)) {
                    response.setSuccess(false);
                    response.setMsg("日报任务内容不能为空或者空白字符串");
                    return response;
                }
                Integer progress = content.getProgress();
                if (Objects.isNull(progress)) {
                    content.setProgress(100);
                } else {
                    if (progress > 100 || progress <= 0) {
                        response.setSuccess(false);
                        response.setMsg("日报任务进度必须为大于0且小于等于100");
                        return response;
                    }
                }
            }
            boolean result = dailyService.insertDaily(dailyList);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    @PostMapping("/update-daily")
    @ApiOperation(value = "更新日报", notes = "更新日报")
    public Response updateDaily(@RequestBody DailyDto content) {
        Response response = new Response();
        try {
            if (Objects.isNull(content.getId())) {
                response.setSuccess(false);
                response.setMsg("日报任务id不能为空");
                return response;
            }
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            content.setUserId(sessionUser.getId());
            String product = content.getProduct();
            if (Objects.isNull(product) || Strings.isEmpty(product) || Strings.isBlank(product)) {
                response.setSuccess(false);
                response.setMsg("所属产品不能为空或者空白字符串");
                return response;
            } else {
                if (!Pattern.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", product)) {
                    response.setSuccess(false);
                    response.setMsg("所属产品必须为2到10位汉字、英文字母、数字");
                    return response;
                }
            }
            String contentStr = content.getContent();
            if (Objects.isNull(contentStr) || Strings.isEmpty(contentStr) || Strings.isBlank(contentStr)) {
                response.setSuccess(false);
                response.setMsg("日报任务内容不能为空或者空白字符串");
                return response;
            }
            Integer progress = content.getProgress();
            if (Objects.isNull(progress)) {
                content.setProgress(100);
            } else {
                if (progress > 100 || progress <= 0) {
                    response.setSuccess(false);
                    response.setMsg("日报任务进度必须为大于0且小于等于100的整数");
                    return response;
                }
            }
            boolean result = dailyService.updateDaily(content);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    @PostMapping("/delete-daily")
    @ApiOperation(value = "删除日报", notes = "删除日报")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "员工id", example = "1", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "id", value = "日报任务id", example = "1", required = true, dataType = "int", paramType = "query")
    })
    public Response deleteDaily(Integer userId, Integer id) {
        Response<DailyContent> response = new Response<>();
        try {
            if (Objects.isNull(id)) {
                response.setSuccess(false);
                response.setMsg("日报任务id不能为空");
                return response;
            }
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            }
            if (Objects.isNull(userId)) {
                userId = sessionUser.getId();
            }
            boolean result = dailyService.deleteDaily(userId, id);
            response.setSuccess(result);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }


    @GetMapping("/list-total-daily")
    @ApiOperation(value = "日报汇总列表", notes = "日报汇总列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "department", value = "部门", example = "技术部", dataType = "String", paramType = "query", required = true),
        @ApiImplicitParam(name = "currentDay", value = "日期", example = "2020-06-01", dataType = "String", paramType = "query", required = true)
    })
    public Response<List<TotalDailyDto>> listTotalDaily(String department, String currentDay) {
        Response<List<TotalDailyDto>> response = new Response<>();
        try {
            DailyUser sessionUser = (DailyUser) httpSession.getAttribute("user");
            String isAdmin = sessionUser.getIsAdmin();
            if (!Objects.equals(isAdmin, "1")) {
                response.setSuccess(false);
                response.setMsg("无权限");
                return response;
            }
            if (Objects.isNull(department) || Strings.isEmpty(department)) {
                response.setSuccess(false);
                response.setMsg("部门不能为空");
                return response;
            }
            if (Objects.isNull(currentDay) || Strings.isEmpty(currentDay)) {
                response.setSuccess(false);
                response.setMsg("日期不能为空");
                return response;
            }
            response.setData(dailyService.listTotalDaily(department, currentDay));
            response.setSuccess(true);
        } catch (Exception e) {
            processException(e, response);
        }
        return response;
    }

    // 处理异常公共方法
    private void processException(Exception e, Response response) {
        e.printStackTrace();
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        log.info("Exception is: {}", e.toString());
        log.info("File is: {}", stackTraceElement.getFileName());
        log.info("Line is: {}", stackTraceElement.getLineNumber());
        log.info("Method is: {}", stackTraceElement.getMethodName());
        response.setSuccess(false);
        response.setMsg("服务器异常");
    }

    // 处理日报内容列表
    private void processDailyList(List<DailyContent> list1, Response<List<Map<String, Object>>> response) {
        String day1 = null;
        List<String> dayList = new ArrayList<>();
        for (DailyContent content : list1) {
            if (Objects.equals(day1, content.getInsertDt().toString().substring(0, 10))) continue;
            day1 = content.getInsertDt().toString().substring(0, 10);
            dayList.add(day1);
        }
        dayList.sort((a, b) -> Integer.parseInt(b.replace("-", "")) - Integer.parseInt(a.replace("-", "")));
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String day : dayList) {
            Map<String, Object> map = new HashMap<>();
            List<DailyContent> list2 = new ArrayList<>();
            for (DailyContent content : list1) {
                content.setDeleteFlag(null);
                String day2 = content.getInsertDt().toString().substring(0, 10);
                if (Objects.equals(day, day2)) {
                    if (Objects.nonNull(content.getId())) {
                        list2.add(content);
                    }
                }
            }
            map.put("day", day);
            map.put("content", list2);
            resultList.add(map);
        }
        response.setSuccess(true);
        response.setData(resultList);
    }

}
