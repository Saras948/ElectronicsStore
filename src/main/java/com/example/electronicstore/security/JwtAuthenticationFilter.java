package com.example.electronicstore.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    private Logger log = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization

        String requestHeader = request.getHeader("Authorization");
        log.info("Request Header : {} " , requestHeader);

        String username = null;
        String token = null;

        if(requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);

            try{
                username = jwtHelper.getUsernameFromToken(token);
                log.info("Username : {} " , username);
            }
            catch (IllegalArgumentException e)
            {
                log.info("Illegal Argument while fetching the username !! ");
                e.printStackTrace();
            }
            catch (ExpiredJwtException e)
            {
                log.info("Given JWT Token has expired");
                e.printStackTrace();
            }
            catch(MalformedJwtException e){
                log.info("SOme changed has done in token !! Invalid Token");
                e.printStackTrace();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }else{
            log.info("Invalid Header value !!");
        }

        //
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Username is not null and Token is not authenticated !!");

            // fetch user details from username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Boolean validToken = jwtHelper.validateToken(token, userDetails);
            if(validToken){
                log.info("Token is valid !!");
                // create authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else {
                log.info("Token is not valid !!");
            }
        }
        else {
            log.info("Username is null and Token is authenticated !!");
        }

        filterChain.doFilter(request, response);
    }
}
