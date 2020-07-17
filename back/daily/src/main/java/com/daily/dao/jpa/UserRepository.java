package com.daily.dao.jpa;

import com.daily.entity.DailyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<DailyUser, Integer>, JpaSpecificationExecutor<DailyUser> {


    Optional<DailyUser> findByAccount(String account);

}
