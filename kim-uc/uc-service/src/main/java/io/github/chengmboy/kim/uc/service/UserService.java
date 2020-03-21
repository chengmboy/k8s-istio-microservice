package io.github.chengmboy.kim.uc.service;

import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import io.github.chengmboy.kim.uc.pojo.dto.UserRegisterDTO;

import java.security.GeneralSecurityException;

/**
 * 用户服务
 * @author cheng_mboy
 */
public interface UserService {

    UserDTO register(UserRegisterDTO registerDTO) throws GeneralSecurityException;

    UserDTO getByLoginName(String loginName);
}
