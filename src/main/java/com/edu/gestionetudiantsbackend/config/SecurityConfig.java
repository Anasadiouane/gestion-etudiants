package com.edu.gestionetudiantsbackend.config;

import com.edu.gestionetudiantsbackend.repository.AdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AdminRepository repo) {
        return username -> repo.findByUsername(username)
                .map(a -> org.springframework.security.core.userdetails.User
                        .withUsername(a.getUsername())
                        .password(a.getPasswordHash())
                        .roles("ADMIN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)       // API locale + Angular
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/actuator/health").permitAll()   // l'Launcher
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())                          // fichiers Angular
                // 401 bla WWW-Authenticate: ma kayban ta popup dyal navigateur, Angular houwa li kaydir l'login
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .httpBasic(b -> b.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }
}
