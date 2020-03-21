package io.github.chengmboy.kim.uc.pojo.repository;

import io.github.chengmboy.kim.uc.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author cheng_mboy
 */
public interface UserRepository extends JpaRepository<User,Long>{

    User findByLoginName(String loginName);
}
