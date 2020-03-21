package io.github.chengmboy.kim.common.config;/*
package io.github.chengmboy.kim.common.config;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

*/
/**
 * @author cheng_mboy
 *//*

@Configuration
@EnableJpaAuditing
@Slf4j
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        log.info(">>> auditorProvider ");
        return new SpringSecurityAuditorAware();
    }

    private class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Optional<Object> auth = Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal);
            if (!auth.isPresent() || auth.get() instanceof String){
                return Optional.of("System");
            }
            return auth
                    .map(Jwt.class::cast)
                    .map(Jwt::getSubject);
        }
    }
}
*/
