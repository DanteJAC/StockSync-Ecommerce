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
    private final AuthenticationProvider authenticationProvider; // Viene de tu ApplicationConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Esto permite que el fetch de JS envíe datos
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Archivos estáticos del frontend (si los sirvieras desde Spring)
                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/icons.svg").permitAll()

                        // Endpoints de autenticación
                        .requestMatchers("/api/login", "/api/register").permitAll()

                        // Protegemos todo el árbol de rutas de la API usando asteriscos dobles
                        .requestMatchers("/api/v1/**", "/api/users/**", "/api/admin/**").authenticated()

                        // Cualquier otra petición que no esté explícitamente permitida arriba, SE BLOQUEA
                        .anyRequest().authenticated()
                )
                // 4. MANEJO DE SESIÓN: Stateless porque usamos tokens
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. FILTROS Y PROVIDER
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}