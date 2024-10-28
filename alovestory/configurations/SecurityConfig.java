package anh.nguyen.alovestory.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login", "/user/register").permitAll() // Publicly accessible endpoints
                        .requestMatchers("/post/like/**", "/post/stream", "/post/comment/**", "/home").authenticated() // Authenticated
                        // endpoints
                        .anyRequest().authenticated() // Catch-all for all other requests, requiring authentication
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Trang đăng nhập tùy chỉnh
                        .defaultSuccessUrl("/home", true) // Chuyển hướng sau khi đăng nhập thành công
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Chuyển hướng sau khi đăng xuất
                        .permitAll())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1) // Giới hạn số phiên người dùng
                        .maxSessionsPreventsLogin(true) // Ngăn đăng nhập nếu đạt giới hạn phiên
                        .expiredUrl("/login?expired") // URL khi phiên hết hạn
                );

        return http.build();
    }

}
