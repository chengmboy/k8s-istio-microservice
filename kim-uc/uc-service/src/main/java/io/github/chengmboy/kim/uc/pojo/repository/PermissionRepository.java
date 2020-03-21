package io.github.chengmboy.kim.uc.pojo.repository;

import io.github.chengmboy.kim.uc.pojo.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
