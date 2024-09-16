package com.genuinecoder.learnspringsecurity;
import com.genuinecoder.learnspringsecurity.model.MyUserDetailService;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
//SecurityConfiguration.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

 @Autowired
 private MyUserRepository myUserRepository;

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
     return httpSecurity
         .csrf(csrf -> csrf
             .ignoringRequestMatchers("/oauth2/**", "/update", "/job-postings/**", "/admin/**"))
         .cors(cors -> cors
             .configurationSource(corsConfigurationSource()))
         .authorizeHttpRequests(authz -> authz
             .requestMatchers("/home", "/register/**", "/login").permitAll()
             .requestMatchers("/admin/**").hasRole("ADMIN")
             .requestMatchers("/user/**").hasRole("USER")
             .requestMatchers("/update").authenticated()
             .anyRequest().authenticated())
         .oauth2Login(oauth2 -> oauth2
             .loginPage("http://localhost:3000/login") // Redirect to React login page
             .successHandler(oauth2AuthenticationSuccessHandler())
             .permitAll())
         .sessionManagement(session -> 
             session
                 .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                 .sessionFixation().migrateSession()
                 .maximumSessions(1)
                 .expiredUrl("/login?error=expired"))
         .logout(logout -> logout
             .logoutUrl("/logout")
             .logoutSuccessUrl("http://localhost:3000/login?logout") // Redirect to React logout page
             .invalidateHttpSession(true)
             .deleteCookies("JSESSIONID"))
         .build();
 }

 @Bean
 public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
     return new OAuth2AuthenticationSuccessHandler(myUserRepository);
 }

 @Bean
 public CorsConfigurationSource corsConfigurationSource() {
     CorsConfiguration configuration = new CorsConfiguration();
     configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
     configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
     configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
     configuration.setAllowCredentials(true);

     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
     source.registerCorsConfiguration("/**", configuration);
     return source;
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }
}