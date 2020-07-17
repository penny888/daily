package com.daily.dao.mybatis;

import com.daily.dto.DailyDto;
import com.daily.dto.TotalDailyDto;
import com.daily.entity.DailyContent;

import java.util.List;


public interface DailyMapper {

    List<DailyContent> searchDaily(DailyDto dailyDto);

    int countDaily(DailyDto dailyDto);

    List<TotalDailyDto> listTotalDaily(String department, String currentDay);

}
