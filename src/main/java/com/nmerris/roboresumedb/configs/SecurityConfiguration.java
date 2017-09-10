package com.nmerris.roboresumedb.configs;

import com.nmerris.roboresumedb.repositories.PersonRepo;
import com.nmerris.roboresumedb.services.SSUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SSUserDetailsService userDetailsService;

    @Autowired
    private PersonRepo personRepo;


    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUserDetailsService(personRepo);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()

                // anyone can access
                .antMatchers("/css/**", "/js/**", "/fonts/**", "/img/**", "/register", "/")
                    .permitAll()

                .antMatchers("/add*", "/startover", "/editdetails", "/delete/*", "/update/*", "/finalresume",
                        "/course*", "/student*", "/summary")
                    .access("hasRole('ROLE_USER') or hasRole('ROLE_RECRUITER')")
                .anyRequest().authenticated();


        // login/out
        http
                .formLogin().failureUrl("/login?error") // thymeleaf can conveniently pick up the login errors in the template
                // there is NO post route for /login, I tried and it never gets called, instead the defaultSuccessUrl route gets called
                .defaultSuccessUrl("/summary")
                .and()
                .formLogin().loginPage("/login")
                    .permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                    .permitAll();

        http
                .csrf().disable();

        http
                .headers().frameOptions().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceBean());
    }

}