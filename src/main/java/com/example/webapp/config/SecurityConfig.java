package com.example.webapp.config;

import com.example.webapp.entity.User;
import com.example.webapp.repository.UserRepository;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/login", "/logout", "/css/**", "/js/**", "/h2-console/**").permitAll()
                    .requestMatchers("/", "/students", "/courses", "/departments").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                    .requestMatchers("/students/edit/**").hasRole("ADMIN")
                    .requestMatchers("/students/delete/**").hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers("/teachers").hasAnyRole("TEACHER", "ADMIN") // Both can view teacher list
                    .requestMatchers("/teachers/edit/**").hasRole("ADMIN") // Only ADMIN can edit teachers
                    .requestMatchers("/teachers/delete/**").hasRole("ADMIN")
                    .requestMatchers("/departments/new", "/departments/edit/**", "/departments/delete/**").hasRole("ADMIN")
                    .requestMatchers("/courses/new", "/courses/edit/**", "/courses/delete/**").hasAnyRole("TEACHER", "ADMIN")
                    .anyRequest().authenticated() // All other authenticated requests
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        // Disable CSRF for H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        // Enable frames for H2 console
        http.headers(headers -> headers.frameOptions().sameOrigin());


        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            String[] roles = user.getRoles().stream().map(role -> role.getName().substring("ROLE_".length())).toArray(String[]::new);

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles)
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
