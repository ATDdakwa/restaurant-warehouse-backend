package com.vozhe.jwt.config;

import com.vozhe.jwt.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userDetailsService;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtRequestFilter jwtRequestFilter;


    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/api/authenticate/",
            "/whitelist/**",
            "/api/warehouse/**",
            "/api/settings/**",
            "/api/meats/**",
            "/api/products/**",
            "/api/payment-settings/**"

    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderBean());
    }
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://0.0.0.0:*", "*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization")
                    .allowCredentials(true);
        }
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .cors()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}


