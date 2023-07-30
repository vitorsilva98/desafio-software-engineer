package com.wallet.operations.configurations.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wallet.operations.configurations.security.services.TokenService;
import com.wallet.operations.configurations.security.services.UserDetailsServiceImpl;
import com.wallet.operations.constants.TokenClaimsConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {    
        String tokenJWT = getAuthorizationHeader(request);

        if (tokenJWT != null) {
            try {
                String subject = tokenService.getSubject(tokenJWT);
                UserDetails user = userDetailsService.loadUserByUsername(subject);

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute(TokenClaimsConstants.USER_ID_CLAIM, tokenService.getUserIdClaim(tokenJWT));
                request.setAttribute(TokenClaimsConstants.USER_EMAIL_CLAIM, tokenService.getSubject(tokenJWT));
            } catch (Exception e) {
                LOGGER.info(String.format("Error validating Authorization header = %s", e.getMessage()));
            }
        } else {
            LOGGER.info("Missing Authorization header");
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authorizationHeader != null) {
            String[] result = authorizationHeader.split(" ");

            if (result.length == 2 && result[0].equals("Bearer")) {
                return result[1];
            }
        }
        
        return null;
    }
}
