package com.stockSync.backend.security.config;

import com.stockSync.backend.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // Inyectamos el nuevo filtro
    private final AuthenticationProvider authenticationProvider; // Viene de ApplicationConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 1. Archivos estáticos de tu Frontend (Vue)
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/icons.svg"
                        ).permitAll()

                        // 2. Endpoints de autenticación de tu API (CORREGIDOS con el prefijo /api)
                        // Ajusta esto si tu controlador de auth usa /api/login o /api/v1/auth/login
                        .requestMatchers(
                                "/api/login",
                                "/api/register"
                        ).permitAll()

                        // 3. Documentación de Swagger/OpenAPI (Acceso público global)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 4. Proteger estrictamente los endpoints de negocio de la API
                        .requestMatchers("/api/v1/**", "/api/users/**", "/api/admin/**").authenticated()

                        // 5. Permitir que Vue maneje cualquier otra ruta de la SPA (ej: /admin, /dashboard)
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}