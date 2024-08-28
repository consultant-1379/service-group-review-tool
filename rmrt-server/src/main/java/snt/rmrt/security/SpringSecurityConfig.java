package snt.rmrt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import snt.rmrt.security.handlers.AuthSuccessHandlerImpl;
import snt.rmrt.security.handlers.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationProvider authProvider;

    @Autowired
    public SpringSecurityConfig(CustomAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/validation").permitAll()
                .antMatchers(HttpMethod.POST, "/api/readyReckoner").permitAll()
                .antMatchers(HttpMethod.POST, "/api/newLoadDrivers").permitAll()
                .antMatchers(HttpMethod.POST, "/api/quickEval").permitAll()
                .antMatchers(HttpMethod.POST, "/api/testCases/addTestCase").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/testCases/**").permitAll()
                .antMatchers(HttpMethod.PATCH,"/api/testCases/**").permitAll()
                .antMatchers(HttpMethod.DELETE,"/api/testCases/**").permitAll()
                .antMatchers("/view/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()

                .formLogin()
                .loginPage("/view/login")
//                .loginProcessingUrl("/perform_login")

                .successHandler(new AuthSuccessHandlerImpl())

//                .failureUrl("/view/login?error=true")
//                .failureHandler(authenticationFailureHandler())
                .and()

                .logout()
//                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
//                .logoutSuccessHandler(logoutSuccessHandler())
        ;
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}