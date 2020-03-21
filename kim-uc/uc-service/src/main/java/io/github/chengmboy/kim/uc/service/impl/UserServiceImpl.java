package io.github.chengmboy.kim.uc.service.impl;

import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import io.github.chengmboy.kim.uc.pojo.dto.UserRegisterDTO;
import io.github.chengmboy.kim.uc.pojo.entity.User;
import io.github.chengmboy.kim.uc.pojo.repository.UserRepository;
import io.github.chengmboy.kim.uc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

/**
 * @author cheng_mboy
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public UserDTO register(UserRegisterDTO registerDTO) throws GeneralSecurityException {
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        userRepository.save(user);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public UserDTO getByLoginName(String loginName) {
        User user = userRepository.findByLoginName(loginName);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static void main(String[] args) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encode = encoder.encode("123456");
        System.out.println(encode);
    }
}
