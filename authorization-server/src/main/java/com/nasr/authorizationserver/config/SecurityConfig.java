package com.nasr.authorizationserver.config;

import com.nasr.authorizationserver.filter.AccessTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AccessTokenAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests()
                .mvcMatchers("/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults());

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void bindAuthenticationProvider(AuthenticationManagerBuilder auth, AuthenticationProvider authenticationProvider)  {
        auth.authenticationProvider(authenticationProvider);
    }

}
