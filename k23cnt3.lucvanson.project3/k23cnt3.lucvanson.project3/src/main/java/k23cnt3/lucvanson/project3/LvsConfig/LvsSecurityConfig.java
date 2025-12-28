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
                                                                "/fonts/**", "/uploads/**") // Added /uploads/** for
                                                                                            // uploaded files
                                                .permitAll()
                                                // Các trang public
                                                .requestMatchers("/", "/home", "/about", "/lvsforum").permitAll()
                                                // Auth pages - IMPORTANT: Permit all login/register endpoints
                                                .requestMatchers("/LvsAuth/**", "/LvsLogin", "/LvsRegister", "/error",
                                                                "/403",
                                                                "/LvsAdmin/LvsLogin", "/LvsUser/LvsLogin",
                                                                "/LvsUser/LvsRegister", "/LvsUser/LvsForgotPassword")
                                                .permitAll()
                                                // Logout endpoints
                                                .requestMatchers("/LvsAdmin/LvsLogout", "/LvsUser/LvsLogout",
                                                                "/LvsLogout")
                                                .permitAll()
                                                // Public user pages - can view without login
                                                .requestMatchers("/lvsforum", "/LvsUser/LvsDashboard", "/LvsUser/",
                                                                "/LvsUser")
                                                .permitAll()
                                                // Search endpoints - allow anonymous search
                                                .requestMatchers("/LvsUser/api/search", "/LvsUser/LvsSearch")
                                                .permitAll()
                                                // Admin pages - yêu cầu ROLE_ADMIN
                                                .requestMatchers("/LvsAdmin/**").hasAuthority("ROLE_ADMIN")
                                                // Moderator pages - yêu cầu ROLE_MODERATOR
                                                .requestMatchers("/LvsModerator/**").hasAuthority("ROLE_MODERATOR")
                                                // Other User pages - yêu cầu authenticated (any logged in user)
                                                .requestMatchers("/LvsUser/**").authenticated()
                                                // Tất cả các request khác có thể truy cập
                                                .anyRequest().permitAll())
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
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        // Check where logout came from
                                                        String referer = request.getHeader("Referer");
                                                        if (referer != null && (referer.contains("/LvsAdmin")
                                                                        || referer.contains("/LvsModerator"))) {
                                                                // Logout from admin/moderator → go to login
                                                                response.sendRedirect(request.getContextPath()
                                                                                + "/LvsUser/LvsLogin");
                                                        } else {
                                                                // Logout from user area → go to login
                                                                response.sendRedirect(request.getContextPath()
                                                                                + "/LvsUser/LvsLogin");
                                                        }
                                                })
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/403") // Trang 403
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        // ALL logins go to user login page
                                                        // Check if session expired
                                                        if (request.getRequestedSessionId() != null
                                                                        && !request.isRequestedSessionIdValid()) {
                                                                response.sendRedirect(request.getContextPath()
                                                                                + "/LvsUser/LvsLogin?session=expired");
                                                        } else {
                                                                response.sendRedirect(request.getContextPath()
                                                                                + "/LvsUser/LvsLogin");
                                                        }
                                                }))
                                .authenticationProvider(authenticationProvider());
                // .csrf(csrf -> csrf.disable()); // Enable CSRF for production

                return http.build();
        }
}
