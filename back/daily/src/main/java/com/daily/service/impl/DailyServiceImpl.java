package com.daily.service.impl;

import com.daily.dao.jpa.DailyRepository;
import com.daily.dao.mybatis.DailyMapper;
import com.daily.dto.DailyDto;
import com.daily.dto.Response;
import com.daily.dto.TotalDailyDto;
import com.daily.entity.DailyContent;
import com.daily.service.DailyService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DailyServiceImpl implements DailyService {

    @Resource
    private DailyRepository dailyRepository;

    @Resource
    private DailyMapper dailyMapper;

    @Override
    public Response<List<DailyContent>> listDaily(int userId, int pageNum, int pageSize) throws Exception {
        Response<List<DailyContent>> response = new Response<>();
        List<DailyContent> list = dailyRepository.findAll((Specification<DailyContent>) (content, q, cb) -> {
            Predicate restrictions = cb.conjunction();
            restrictions = cb.and(restrictions, cb.equal(content.get("userId"), userId));
            q.where(restrictions);
            q.orderBy(cb.desc(content.get("insertDt")));
            return q.getRestriction();
        });
        List<DailyContent> resultList = new ArrayList<>();
        if (list.size() > 0) {
            processDailyList(list, resultList, pageNum, pageSize);
        }
        int total  = dailyRepository.countByUserIdGroupByInsertDt(userId);
        response.setData(resultList);
        response.setTotal(total);
        return response;
    }

    @Override
    public Response<List<DailyContent>> searchDaily(DailyDto dailyDto) throws Exception {
        Response<List<DailyContent>> response = new Response<>();
        int pageNum = dailyDto.getPageNum();
        int pageSize = dailyDto.getPageSize();
        dailyDto.setPageNum(null);
        dailyDto.setPageSize(null);
        List<DailyContent> list = dailyMapper.searchDaily(dailyDto);
        List<DailyContent> resultList = new ArrayList<>();
        if (list.size() > 0) {
            processDailyList(list, resultList, pageNum, pageSize);
        }
        response.setData(resultList);
        int total = dailyMapper.countDaily(dailyDto);
        response.setTotal(total);
        return response;

    }

    @Override
    public DailyContent getDaily(Integer userId, int id) {
        Optional<DailyContent> optional;
        if (Objects.isNull(userId)) {
            optional = dailyRepository.findById(id);
        } else {
            optional = dailyRepository.findByIdAndUserId(id, userId);
        }
        return optional.orElse(null);
    }

    @Override
    public boolean insertDaily(List<DailyDto> list) {
        List<DailyContent> list1 = new ArrayList<>();
        DailyContent content1;
        for (DailyDto content: list) {
            content1 = new DailyContent();
            BeanUtils.copyProperties(content, content1, "insertDt");
            String insertDt = content.getInsertDt();
            if (Objects.nonNull(insertDt) && Strings.isNotEmpty(insertDt) && Strings.isNotBlank(insertDt)) {
                content1.setInsertDt(Timestamp.valueOf(insertDt));
            } else {
                content1.setInsertDt(new Timestamp(System.currentTimeMillis()));
            }
            content1.setDeleteFlag("0");
            list1.add(content1);
        }
        dailyRepository.saveAll(list1);
        return true;
    }

    @Override
    public boolean updateDaily(DailyDto content) {
        Optional<DailyContent> optional = dailyRepository.findByIdAndUserId(content.getId(), content.getUserId());
        if (optional.isPresent()) {
            DailyContent entity = optional.get();
            entity.setProduct(content.getProduct());
            entity.setProgress(content.getProgress());
            entity.setContent(content.getContent());
            String note = content.getNote();
            if (Objects.nonNull(note) && Strings.isNotEmpty(note) && Strings.isNotBlank(note)) {
                entity.setNote(note);
            }
            String insertDt = content.getInsertDt();
            if (Objects.nonNull(insertDt) && Strings.isNotEmpty(insertDt) && Strings.isNotBlank(insertDt)) {
                entity.setInsertDt(Timestamp.valueOf(insertDt));
            }
            dailyRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDaily(Integer userId, int id) {
        Optional<DailyContent> optional;
        if (Objects.isNull(userId)) {
            optional = dailyRepository.findById(id);
        } else {
            optional = dailyRepository.findByIdAndUserId(id, userId);
        }
        if (optional.isPresent()) {
            DailyContent entity = optional.get();
            entity.setDeleteFlag("1");
            dailyRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public List<TotalDailyDto> listTotalDaily(String department, String currentDay) {
       return dailyMapper.listTotalDaily(department, currentDay);
    }

    // 日报列表处理
    private void processDailyList(List<DailyContent> totalList, List<DailyContent> resultList, int pageNum, int pageSize) {
        List<String> dayList = new ArrayList<>();
        for (DailyContent content : totalList) {
            if (!dayList.contains(content.getInsertDt().toString().substring(0, 10))) {
                dayList.add(content.getInsertDt().toString().substring(0, 10));
            }
        }
        System.out.println(dayList);
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;
        if (end > dayList.size()) {
            end = dayList.size();
        }
        List<String> subList = dayList.subList(start, end);
        for (DailyContent content : totalList) {
            if (subList.contains(content.getInsertDt().toString().substring(0, 10))) {
                resultList.add(content);
            }
        }
    }
}
