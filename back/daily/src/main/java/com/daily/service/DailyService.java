package com.daily.service;

import com.daily.dto.DailyDto;
import com.daily.dto.Response;
import com.daily.dto.TotalDailyDto;
import com.daily.entity.DailyContent;

import java.util.List;


public interface DailyService {

    Response<List<DailyContent>> listDaily(int userId, int pageNum, int pageSize) throws Exception;

    Response<List<DailyContent>> searchDaily(DailyDto dailyDto) throws Exception;

    DailyContent getDaily(Integer userId, int id);

    boolean insertDaily(List<DailyDto> list);

    boolean updateDaily(DailyDto content);

    boolean deleteDaily(Integer userId, int id);

    List<TotalDailyDto> listTotalDaily(String department, String currentDay);

}
