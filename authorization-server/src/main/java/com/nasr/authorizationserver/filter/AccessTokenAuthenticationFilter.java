package com.nasr.authorizationserver.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nasr.authorizationserver.constant.ConstantField.BEARER_PREFIX;

@Component
@Log4j2
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("bearerTokenAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/users")) {
            String auth = request.getHeader("Authorization");


            if (auth!=null && auth.startsWith(BEARER_PREFIX)){
                String token = auth.replace(BEARER_PREFIX, "");
                BearerTokenAuthenticationToken bearerToken = new BearerTokenAuthenticationToken(token);

                try {
                    Authentication authenticate = authenticationManager.authenticate(bearerToken);
                    SecurityContextHolder.getContext().setAuthentication(authenticate);

                    filterChain.doFilter(request, response);
                } catch (Exception e) {

                    log.error("error on decode token");
                    response.getOutputStream().write("invalid token".getBytes());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
            else {
                response.getOutputStream().write("invalid token !!!".getBytes());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } else filterChain.doFilter(request, response);


    }
}
