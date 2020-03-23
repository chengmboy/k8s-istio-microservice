package io.github.chengmboy.kim.payment.pojo.repository;

import io.github.chengmboy.kim.payment.pojo.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

/**
 * @author cheng_mboy
 */
public interface BalanceRepository extends JpaRepository<Balance,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    Balance getOne(Long id);

    Balance findByUserId(Long userId);
}
