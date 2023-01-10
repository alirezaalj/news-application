package ir.alirezaalijani.news.application.security.config;


import ir.alirezaalijani.news.application.security.service.TokenService;
import ir.alirezaalijani.news.application.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component("tokenFilter")
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_BEARER="Bearer ";

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public AuthTokenFilter(TokenService tokenService,
                           @Qualifier("customUserDetails") UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println(request.getRemoteAddr()+" -> URI:"+request.getRequestURI()+" "+request.getMethod().toUpperCase());
        HttpUtil.printRequestInfo(request);
        try {
            String jwt = parseJwt(request);
            if (jwt != null && tokenService.validateToken(jwt)) {
                String username = tokenService.getTokenSubject(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_BEARER)) {
            return  headerAuth.substring(7);
        }
        return null;
    }

}
