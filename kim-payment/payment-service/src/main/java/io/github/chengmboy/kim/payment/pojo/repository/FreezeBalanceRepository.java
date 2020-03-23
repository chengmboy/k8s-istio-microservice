package io.github.chengmboy.kim.payment.pojo.repository;

import io.github.chengmboy.kim.payment.pojo.entity.FreezeBalance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author cheng_mboy
 */
public interface FreezeBalanceRepository  extends JpaRepository<FreezeBalance,Long>{

}
