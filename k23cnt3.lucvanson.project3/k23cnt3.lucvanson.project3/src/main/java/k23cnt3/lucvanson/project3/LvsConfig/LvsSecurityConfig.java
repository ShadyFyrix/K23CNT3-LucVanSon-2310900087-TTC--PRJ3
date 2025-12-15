package k23cnt3.lucvanson.project3.LvsConfig;

import k23cnt3.lucvanson.project3.LvsService.LvsUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class LvsSecurityConfig {

        @Autowired
        private LvsUserDetailsServiceImpl userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Các tài nguyên tĩnh
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**",
                                                                "/fonts/**")
                                                .permitAll()
                                                // Các trang public
                                                .requestMatchers("/", "/home", "/about", "/lvsforum").permitAll()
                                                // Auth pages - IMPORTANT: Permit all login/register endpoints
                                                .requestMatchers("/LvsLogin", "/LvsRegister", "/error", "/403",
                                                                "/LvsAdmin/LvsLogin", "/LvsUser/LvsLogin",
                                                                "/LvsUser/LvsRegister", "/LvsUser/LvsForgotPassword")
                                                .permitAll()
                                                // Logout endpoints
                                                .requestMatchers("/LvsAdmin/LvsLogout", "/LvsUser/LvsLogout")
                                                .permitAll()
                                                // Admin pages - yêu cầu ROLE_ADMIN
                                                .requestMatchers("/LvsAdmin/**").hasAuthority("ROLE_ADMIN")
                                                // Moderator pages - yêu cầu ROLE_MODERATOR
                                                .requestMatchers("/LvsModerator/**").hasAuthority("ROLE_MODERATOR")
                                                // User pages - yêu cầu authenticated
                                                .requestMatchers("/LvsUser/**").authenticated()
                                                // Tất cả các request khác cần xác thực
                                                .anyRequest().authenticated())
                                // DISABLE Spring Security form login - use custom controller instead
                                // .formLogin(form -> form
                                // .loginPage("/LvsLogin")
                                // .loginProcessingUrl("/LvsLogin")
                                // .usernameParameter("LvsUsername")
                                // .passwordParameter("LvsPassword")
                                // .defaultSuccessUrl("/LvsUser/LvsDashboard", true)
                                // .failureUrl("/LvsLogin?error=true")
                                // .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/LvsLogout") // URL để logout
                                                .logoutSuccessUrl("/LvsLogin?logout=true") // Sau logout thành công
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/403") // Trang 403
                                )
                                .authenticationProvider(authenticationProvider());
                // .csrf(csrf -> csrf.disable()); // Enable CSRF for production

                return http.build();
        }
}
