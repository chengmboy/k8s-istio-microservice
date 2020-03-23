package io.github.chengmboy.kim.payment.pojo.entity;

import io.github.chengmboy.kim.common.web.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * 冻结余额
 * @author cheng_mboy
 */
@Data
@Entity
public class FreezeBalance extends AbstractEntity{

    /**
     * 账户余额
     * */
    @ManyToOne
    private Balance balance;

    /**
     * 冻结数量
     * */
    @Column(precision = 10,scale = 2)
    private BigDecimal freeze = BigDecimal.ZERO;
}
