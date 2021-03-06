package com.notes.backend.configs;


import com.notes.backend.auth.AuthEntryPointJwt;
import com.notes.backend.filters.AuthTokenFilter;
import com.notes.backend.services.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.DelegatingFilterProxy;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    final AuthEntryPointJwt unauthorizedHandler;
    final UserDetailsServiceImpl userDetailsService;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new SecurityProxy();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/h2-console", "/h2-console/**", "/swagger-ui/**").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/resources/**", "/resources/static/**", "/resources/static/img/**", "/resources/static/static/css/*.css", "/resources/static/static/js/*.js").permitAll()
                .antMatchers("/img/**", "/static/**", "/static/img/**", "/static/static/css/*.css", "/static/static/js/*.js").permitAll()
                .antMatchers("/*.js").permitAll()
                .antMatchers("/*.css").permitAll()
                .antMatchers("/*.ico").permitAll()
                .antMatchers("/*.json").permitAll()
                .antMatchers("/*.html").permitAll()
                .antMatchers("/*.png").permitAll()
                .antMatchers("/*.svg").permitAll()
                .antMatchers("/*.ttf").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/login", "/api/login/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users", "/api/users/").permitAll()
                .anyRequest().authenticated();

        http.headers().frameOptions().sameOrigin();
        http.addFilterBefore(delegatingFilterProxy(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}