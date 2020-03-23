package io.github.chengmboy.kim.payment.pojo.entity;

import io.github.chengmboy.kim.common.web.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author cheng_mboy
 */
@Data
@Entity
public class Balance extends AbstractEntity{

    private Long userId;

    @Column(precision = 10,scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "balance")
    List<FreezeBalance> freezeBalances;

}
