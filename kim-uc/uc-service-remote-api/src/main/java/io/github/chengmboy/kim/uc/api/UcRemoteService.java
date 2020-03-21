package io.github.chengmboy.kim.uc.api;


import io.github.chengmboy.kim.uc.api.constants.UcConstants;
import io.github.chengmboy.kim.uc.api.dto.PermissionDTO;
import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import io.github.chengmboy.kim.uc.api.fallback.UcRemoteFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name =UcConstants.kimUc , url = UcConstants.kimUc,fallback = UcRemoteFallbackImpl.class)
public interface UcRemoteService {

    @GetMapping("/permission/{roleCode}")
    Set<PermissionDTO> findPermissionByRoleCode(@PathVariable("roleCode") String roleCode);

    @GetMapping("/user/login/{loginName}")
    UserDTO getByLoginName(@PathVariable("loginName") String loginName);
}
