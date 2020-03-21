package io.github.chengmboy.kim.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@EnableWebSecurity
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
/*    @Resource
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;
    @Autowired
    private MobileSecurityConfigurer mobileSecurityConfigurer;*/

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
                http.authorizeRequests();
//        filterIgnorePropertiesConfig.getUrls().forEach(url -> registry.antMatchers(url).permitAll());
        registry.antMatchers("/health").permitAll();
        registry.anyRequest().authenticated()
                .and()
                .csrf().disable();
        //配置手机号获取token
//        http.apply(mobileSecurityConfigurer);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
