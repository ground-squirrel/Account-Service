package account.business.security;

import account.business.AccountDetailsService;
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

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountDetailsService service;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(service)
                .passwordEncoder(getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint) // Handle auth errors
                .and()
                .csrf().disable().headers().frameOptions().disable() // For Postman, the H2 console
                .and()
                .authorizeRequests() // Manage access
                .mvcMatchers("/api/auth/signup").permitAll()
                .mvcMatchers("/actuator/shutdown").permitAll()
                .mvcMatchers("/**").authenticated()
//                .mvcMatchers("/api/auth/changepass").authenticated()
//                .mvcMatchers("/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
//                .mvcMatchers("/api/acct/**").hasRole("ACCOUNTANT")
//                .mvcMatchers("/api/admin/**").hasRole("ADMIN")
                // other matchers
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
