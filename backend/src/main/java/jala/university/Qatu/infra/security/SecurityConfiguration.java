package jala.university.Qatu.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    @Autowired
    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/products/recommendation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/addvisit/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("SELLER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/seller").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/seller/active").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/seller/banned").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/seller/ban/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/seller/unban/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/seller/revoke/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/api/seller-applications").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/api/seller-applications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/api/seller-applications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/api/seller-applications/pending").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user/application").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user/").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user/application/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/profile").permitAll()

                        .requestMatchers("/api/cart/**").permitAll()

                        .requestMatchers("/api/order/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers("/api/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/rating").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rating/**").permitAll()

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws  Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
