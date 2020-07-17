package com.daily.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Where( clause = "delete_flag = '0'" )
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "daily_content", schema = "test")
public class DailyContent {
    private Integer id;
    private Integer userId;
    private String product;
    private String content;
    private Integer progress;
    private String note;
    private Timestamp insertDt;
    private String deleteFlag;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "product", nullable = false, length = 255)
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Basic
    @Column(name = "content", nullable = false, length = -1)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "progress", nullable = true)
    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Basic
    @Column(name = "note", nullable = true)
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Basic
    @Column(name = "insert_dt", nullable = false)
    public Timestamp getInsertDt() {
        return insertDt;
    }

    public void setInsertDt(Timestamp insertDt) {
        this.insertDt = insertDt;
    }

    @Basic
    @Column(name = "delete_flag", nullable = true, length = 1)
    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyContent that = (DailyContent) o;
        return id.equals(that.id) &&
            userId.equals(that.userId) &&
            product.equals(that.product) &&
            content.equals(that.content) &&
            Objects.equals(progress, that.progress) &&
            Objects.equals(note, that.note) &&
            insertDt.equals(that.insertDt) &&
            Objects.equals(deleteFlag, that.deleteFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, product, content, progress, note, insertDt, deleteFlag);
    }
}
