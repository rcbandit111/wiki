package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize and @PostAuthorize annotations
@Profile("!dev")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // HttpSecurity is the first layer

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();
        http.cors().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()
            .antMatchers("/users/authorize").permitAll()
            .antMatchers("/users/refresh").permitAll()
            .antMatchers("/users/reset_request").permitAll()
            .antMatchers("/users/reset_token").permitAll()
            .antMatchers("/users/reset_password").permitAll()
            .antMatchers("/users/confirmation_token").permitAll()
            .antMatchers("/users/reset_user_password").permitAll()
            // Disallow everything else..
            .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        // Apply JWT
        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        // WebSecurity is the second layer
        web.ignoring()
                .antMatchers("/users/authorize")
                .antMatchers("/users/refresh")
                .antMatchers("/users/reset_request")
                .antMatchers("/users/reset_token")
                .antMatchers("/users/reset_password")
                .antMatchers("/users/confirmation_token")
                .antMatchers("/users/reset_user_password");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

}
