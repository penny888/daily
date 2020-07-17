package com.daily.dao.jpa;

import com.daily.entity.DailyContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyRepository extends JpaRepository<DailyContent, Integer>, JpaSpecificationExecutor<DailyContent> {

    Optional<DailyContent> findByIdAndUserId(Integer userId, int id);

    @Query(value = "select count(distinct left(insert_dt, 10)) from daily_content where delete_flag = '0' and user_id = ?1", nativeQuery = true)
    int countByUserIdGroupByInsertDt(Integer userId);

}
