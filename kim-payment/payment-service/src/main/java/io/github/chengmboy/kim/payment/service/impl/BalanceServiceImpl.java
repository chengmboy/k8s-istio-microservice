package io.github.chengmboy.kim.payment.service.impl;

import io.github.chengmboy.kim.common.exception.EntityAlreadyExistException;
import io.github.chengmboy.kim.payment.api.dto.BalanceDTO;
import io.github.chengmboy.kim.payment.api.exception.OutOfMoneyException;
import io.github.chengmboy.kim.payment.pojo.entity.Balance;
import io.github.chengmboy.kim.payment.pojo.entity.FreezeBalance;
import io.github.chengmboy.kim.payment.pojo.repository.BalanceRepository;
import io.github.chengmboy.kim.payment.pojo.repository.FreezeBalanceRepository;
import io.github.chengmboy.kim.payment.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author cheng_mboy
 */
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    final BalanceRepository balanceRepository;
    final FreezeBalanceRepository freezeBalanceRepository;

    @Override
    public BalanceDTO query(Long userId) {
        Balance balance = balanceRepository.findByUserId(userId);
        BalanceDTO balanceDTO = new BalanceDTO();
        BeanUtils.copyProperties(balance ,balanceDTO);
        return balanceDTO;
    }

    @Override
    public BalanceDTO freeze(Long balanceId, BigDecimal num) throws OutOfMoneyException {
        //锁定账户
        Balance balance = balanceRepository.getOne(balanceId);

        //检查余额是否足够
        BigDecimal current = balance.getBalance();
        List<FreezeBalance> freezeBalances = balance.getFreezeBalances();
        BigDecimal freezeCount = freezeBalances.stream()
                .map(FreezeBalance::getFreeze)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal available = current.subtract(freezeCount);
        if (available.compareTo(num) < 0) {
            throw new OutOfMoneyException();
        }
        FreezeBalance freezeBalance = new FreezeBalance();
        freezeBalance.setBalance(balance);
        freezeBalance.setFreeze(num);
        freezeBalanceRepository.save(freezeBalance);
        BalanceDTO balanceDTO = new BalanceDTO();
        BeanUtils.copyProperties(balance ,balanceDTO);
        balanceDTO.setFreezeBalance(freezeCount.add(num));
        return balanceDTO;
    }

    @Override
    public void deduct(Long freezeId){
        FreezeBalance freezeBalance = freezeBalanceRepository.getOne(freezeId);
        Balance balance = balanceRepository.getOne(freezeBalance.getBalance().getId());
        BigDecimal current = balance.getBalance().subtract(freezeBalance.getFreeze());
        balance.setBalance(current);
        balanceRepository.save(balance);
        freezeBalanceRepository.delete(freezeBalance);
    }

    @Override
    public void cancel(Long freezeId) {
        FreezeBalance freezeBalance = freezeBalanceRepository.getOne(freezeId);
        Balance balance = balanceRepository.getOne(freezeBalance.getBalance().getId());
        BigDecimal current = balance.getBalance().add(freezeBalance.getFreeze());
        balance.setBalance(current);
        balanceRepository.save(balance);
        freezeBalanceRepository.delete(freezeBalance);
    }

    @Override
    public BalanceDTO recharge(Long balanceId, BigDecimal num){
        Balance balance = balanceRepository.getOne(balanceId);
        BigDecimal current = balance.getBalance().add(num);
        balance.setBalance(current);
        balanceRepository.save(balance);
        BalanceDTO balanceDTO = new BalanceDTO();
        BeanUtils.copyProperties(balance ,balanceDTO);
        return balanceDTO;
    }

    @Override
    public BalanceDTO create(Long userId) throws EntityAlreadyExistException {
        Balance balance = balanceRepository.findByUserId(userId);
        if (balance != null) {
            throw new EntityAlreadyExistException();
        }
        balance = new Balance();
        balance.setUserId(userId);
        balanceRepository.save(balance);
        BalanceDTO balanceDTO = new BalanceDTO();
        BeanUtils.copyProperties(balance ,balanceDTO);
        return balanceDTO;
    }
}
