package io.github.chengmboy.kim.payment.service;

import io.github.chengmboy.kim.common.exception.EntityAlreadyExistException;
import io.github.chengmboy.kim.payment.api.dto.BalanceDTO;
import io.github.chengmboy.kim.payment.api.exception.OutOfMoneyException;

import java.math.BigDecimal;

/**
 * @author cheng_mboy
 */
public interface BalanceService {

    /**
     * 查询账户
     */
    BalanceDTO query(Long userId);

    /**
     *
     * 冻结金额
     * @param balanceId 用户账户余额Id
     *               @param num 冻结金额
     * */
    BalanceDTO freeze(Long balanceId, BigDecimal num) throws OutOfMoneyException;


    /**
     * 扣减冻结金额
     * @param freezeId 冻结金额id
     * */
    void deduct(Long freezeId);

    /**
     * 取消冻结
     * */
    void cancel(Long freezeId);

    /**
     * 充值
     */
    BalanceDTO recharge(Long balanceId, BigDecimal num);

    /**
     * 创建账户
     */
    BalanceDTO create(Long userId) throws EntityAlreadyExistException;
}
