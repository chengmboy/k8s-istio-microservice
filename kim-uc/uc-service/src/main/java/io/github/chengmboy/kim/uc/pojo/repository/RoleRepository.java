package io.github.chengmboy.kim.uc.pojo.repository;

import io.github.chengmboy.kim.uc.pojo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByRoleCode(String roleCode);
}
