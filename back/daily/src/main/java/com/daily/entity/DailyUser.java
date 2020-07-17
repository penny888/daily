package com.daily.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Where( clause = "delete_flag = '0'" )
@EntityListeners(AuditingEntityListener.class)
@Data
@Entity
@Table(name = "daily_user", schema = "test")
public class DailyUser implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Basic
    @Column(name = "account", nullable = false, length = 255)
    private String account;

    @Basic
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Basic
    @Column(name = "is_admin", nullable = true)
    private String isAdmin;

    @Basic
    @Column(name = "department", nullable = false, length = 255)
    private String department;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Basic
    @Column(name = "insert_dt", nullable = false)
    private Timestamp insertDt;

    @Basic
    @Column(name = "delete_flag", nullable = true, length = 1)
    private String deleteFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyUser dailyUser = (DailyUser) o;
        return id.equals(dailyUser.id) &&
            Objects.equals(name, dailyUser.name) &&
            Objects.equals(account, dailyUser.account) &&
            Objects.equals(password, dailyUser.password) &&
            Objects.equals(isAdmin, dailyUser.isAdmin) &&
            Objects.equals(department, dailyUser.department) &&
            Objects.equals(insertDt, dailyUser.insertDt) &&
            Objects.equals(deleteFlag, dailyUser.deleteFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, account, password, isAdmin, department, insertDt, deleteFlag);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
