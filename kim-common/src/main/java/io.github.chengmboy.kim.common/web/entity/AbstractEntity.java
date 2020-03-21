package io.github.chengmboy.kim.common.web.entity;

import java.sql.Timestamp;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author cheng_mboy
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity extends IdEntity {

    @CreatedBy
    String create_name;

    @LastModifiedBy
    String update_name;

    @CreatedDate
    Timestamp create_date;

    @LastModifiedDate
    Timestamp update_date;

}
