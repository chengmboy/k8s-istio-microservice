package io.github.chengmboy.kim.uc.controller;

import com.alibaba.fastjson.JSONObject;
import io.github.chengmboy.kim.common.web.Response;
import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import io.github.chengmboy.kim.uc.pojo.dto.UserRegisterDTO;
import io.github.chengmboy.kim.uc.pojo.vo.UserVO;
import io.github.chengmboy.kim.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;

/**
 *
 * 用户控制器
 * @author cheng_mboy
 */
@RestController
@RequestMapping("auth/user")
@RequiredArgsConstructor
@Slf4j
@Api
public class UserController {

    private final UserService userService;
    @PostMapping("register")
    public Response<UserVO> register(@Validated @ApiParam @RequestBody UserRegisterDTO user) throws GeneralSecurityException {
        log.info(JSONObject.toJSONString(user));
        UserDTO userDTO = userService.register(user);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDTO,userVO);
        return Response.ok(userVO);
    }

    @PostMapping("login")
    public Response<UserVO> login(@Validated @ApiParam @RequestBody UserRegisterDTO user) throws GeneralSecurityException {

        return Response.ok(null);
    }
}
