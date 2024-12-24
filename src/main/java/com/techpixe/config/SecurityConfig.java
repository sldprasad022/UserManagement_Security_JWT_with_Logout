//package com.techpixe.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig
//{
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity  http) throws Exception {
////        http
////            // Disable CSRF (Cross-Site Request Forgery) if you don't need it
////            .csrf().disable()
////            // Permit all requests (disable login page)
////            .authorizeRequests()
////                .anyRequest().permitAll()
////            .and()
////            // Disable form-based login
////            .formLogin().disable();
////
////        return http.build();
////    }
//    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity  http) throws Exception 
//    {
//        http
//            // Disable CSRF (Cross-Site Request Forgery) if you don't need it
//            .csrf().disable()
//            // Permit all requests (disable login page)
//            .authorizeRequests()
//                .anyRequest().permitAll()
//            .and()
//            // Disable form-based login
//            .formLogin().disable();
//
//        return http.build();
//    }
//}




package com.techpixe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.techpixe.serviceImpl.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http
        	.csrf(csrf -> csrf.disable())
        
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated() // this is best for production environment and it is recommendate to use.here,we will get 401 or 403 HTTP Status code when we hit NON-EXISTENT URL's if we use the .anyRequest().authenticated().
                //.anyRequest().permitAll() // Allow all unmatched paths and it is not recommendated to use and it also not secure.if we use this we will get 404 HTTP Status code when we hit NON-EXISTENT URL's.
            )
            
            .exceptionHandling(e-> e.accessDeniedHandler(customAccessDeniedHandler)
            		.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session management to stateless
                    )
            
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

