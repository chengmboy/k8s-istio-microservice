package io.github.chengmboy.kim.payment.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author cheng_mboy
 */
@Data
public class BalanceDTO {

    private Long userId;

    private BigDecimal balance;

    private BigDecimal freezeBalance;
}
