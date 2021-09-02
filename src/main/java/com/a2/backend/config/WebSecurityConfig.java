package com.a2.backend.config;

import com.a2.backend.service.ApplicationUserService;
import com.a2.backend.service.impl.ApplicationUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.a2.backend.security.AuthenticationFilter;
import com.a2.backend.security.AuthorizationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private ApplicationUserServiceImpl applicationUserServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfig(ApplicationUserService applicationUserService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserServiceImpl = applicationUserServiceImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager()))
                .addFilter(new AuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Bean
    public PasswordEncoder encoder() {
        return this.bCryptPasswordEncoder;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(applicationUserServiceImpl).passwordEncoder(bCryptPasswordEncoder);
    }
}
