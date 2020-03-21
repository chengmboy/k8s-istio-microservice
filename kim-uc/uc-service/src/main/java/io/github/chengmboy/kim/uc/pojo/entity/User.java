package io.github.chengmboy.kim.uc.pojo.entity;

import io.github.chengmboy.kim.common.web.entity.AbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author cheng_mboy
 */
@Data
@Entity
public class User extends AbstractEntity {

    private String name;

    private String email;

    private String phone;

    @OneToMany
    private List<Role> roles;

    private Boolean status;

    @Column(nullable = false,length = 20)
    private  String loginName;

    @Column(nullable = false)
    private String password;

}
