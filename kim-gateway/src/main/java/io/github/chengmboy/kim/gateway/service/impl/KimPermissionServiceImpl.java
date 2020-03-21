package io.github.chengmboy.kim.gateway.service.impl;

import io.github.chengmboy.kim.common.util.IpHelper;
import io.github.chengmboy.kim.gateway.service.KimPermissionService;
import io.github.chengmboy.kim.uc.api.UcRemoteService;
import io.github.chengmboy.kim.uc.api.dto.PermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service("kimPermissionService")
@RequiredArgsConstructor
public class KimPermissionServiceImpl implements KimPermissionService {

    private final UcRemoteService ucRemoteService;

    private final DiscoveryClient discoveryClient;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {

        boolean hasPermission = false;

        List<ServiceInstance> monitorServices = discoveryClient.getInstances("kim-MONITOR");
        List<ServiceInstance> turbineServices = discoveryClient.getInstances("kim-TURBINE");
        Set<String> allowIps = new HashSet<>();
        monitorServices.forEach(i->allowIps.add(i.getHost()));
        turbineServices.forEach(i->allowIps.add(i.getHost()));

        String ip = IpHelper.getIpAddr(request);
        if (allowIps.contains(ip)) {
            //白名单
            return true;
        }


        Object principal = authentication.getPrincipal();
        List<SimpleGrantedAuthority> grantedAuthorityList = (List<SimpleGrantedAuthority>) authentication.getAuthorities();

        if (principal != null) {
            if (CollectionUtils.isEmpty(grantedAuthorityList)) {
                log.warn("角色列表为空：{}", authentication.getPrincipal());
                return hasPermission;
            }

            Set<PermissionDTO> permissionS = new HashSet<>();
            for (SimpleGrantedAuthority authority : grantedAuthorityList) {
                    Set<PermissionDTO> permissionVOSet = ucRemoteService.findPermissionByRoleCode(authority.getAuthority());
                    if (CollectionUtils.isNotEmpty(permissionVOSet)) {
                        permissionS.addAll(permissionVOSet);
                }
            }

            for (PermissionDTO permission : permissionS) {
                if (permission!=null&&!StringUtils.isBlank(permission.getUrl())
                        && antPathMatcher.match(permission.getUrl(), request.getRequestURI())
                        ) {
                    if(permission.getMethod().equalsIgnoreCase("*")
                            || request.getMethod().equalsIgnoreCase(permission.getMethod())) {
                        hasPermission = true;
                        break;
                    }
                }
            }
        }
        return hasPermission;
    }
}
