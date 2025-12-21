package cl.duoc.ejemplo.microservicio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cl.duoc.ejemplo.microservicio.security.jwt.JwtAuthFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.headers(h -> h.frameOptions(f -> f.disable()));
        http.sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/h2-console/**"
            ).permitAll()

            // EVENTOS
            .requestMatchers(HttpMethod.GET, "/eventos/**")
                .hasAnyRole("CLIENTE", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/eventos/compras")
                .hasAnyRole("CLIENTE", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/eventos")
                .hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/eventos/**")
                .hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/eventos/**")
                .hasRole("ADMIN")

            // S3 solo ADMIN
            .requestMatchers("/s3/**")
                .hasRole("ADMIN")

            .anyRequest().authenticated()
        );

        http.addFilterBefore(
            jwtAuthFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    // 👇 USUARIOS EN MEMORIA
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails cliente = User.builder()
                .username("cliente")
                .password(encoder.encode("cliente123"))
                .roles("CLIENTE")
                .build();

        return new InMemoryUserDetailsManager(admin, cliente);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg
    ) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

