package io.github.chengmboy.kim.uc.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author cheng_mboy
 */
@Data
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String password;

    private List<RoleDTO> roles;

    private Boolean status;
}
