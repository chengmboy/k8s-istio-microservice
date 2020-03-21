package io.github.chengmboy.kim.uc.api.fallback;

import io.github.chengmboy.kim.uc.api.UcRemoteService;
import io.github.chengmboy.kim.uc.api.dto.PermissionDTO;
import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class UcRemoteFallbackImpl implements UcRemoteService {


    @Override
    public Set<PermissionDTO> findPermissionByRoleCode(String roleCode) {
        log.error("调用{}异常:{}", "findPermissionByRoleCode", roleCode);
        return null;
    }

    @Override
    public UserDTO getByLoginName(String loginName) {
        log.error("调用{}异常:{}", "getByLoginName", loginName);
        return null;
    }
}
