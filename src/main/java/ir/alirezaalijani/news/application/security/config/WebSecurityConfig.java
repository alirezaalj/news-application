package ir.alirezaalijani.news.application.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final Filter tokenRequestFilter;

    public WebSecurityConfig(@Qualifier("customUserDetails") UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             @Qualifier("tokenFilter")
                                     Filter oncePerRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRequestFilter = oncePerRequestFilter;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return ((request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: UNAUTHORIZED");
        });
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return ((request, response, accessDeniedException) ->{
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error: FORBIDDEN");
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                // header configs only for h2 db
                .headers(headersConfigurer -> headersConfigurer
                                .frameOptions()
                                .sameOrigin()
                                .httpStrictTransportSecurity().disable()
                        )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(
                        expression -> expression
                                // public
                                .antMatchers("/actuator/health","/error").permitAll()
                                .antMatchers("/assets/**").permitAll()
                                .antMatchers("/h2/**").permitAll()
                                .antMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                                .antMatchers("/","/index/health").permitAll()
                                // private
                                .antMatchers(HttpMethod.POST,"/api/v1/auth/register").hasRole("ADMIN")
                                .antMatchers(HttpMethod.POST,"/api/v1/auth/register").hasIpAddress("127.0.0.1")
                                .antMatchers(HttpMethod.POST,"/api/v1/auth/update").hasAnyRole("ADMIN","USER")
                                .antMatchers(HttpMethod.POST,"/api/v1/auth/users").hasAnyRole("ADMIN","USER")
                                .antMatchers(HttpMethod.GET,"/api/v1/news/**").hasAnyRole("USER","ADMIN")
                                .antMatchers(HttpMethod.POST,"/api/v1/news/**").hasRole("ADMIN")
                                .antMatchers("/api/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()

                ).cors();
        http.addFilterBefore(tokenRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
