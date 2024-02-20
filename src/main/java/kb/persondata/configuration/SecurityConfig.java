package kb.persondata.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .formLogin().disable()
                .authorizeHttpRequests().requestMatchers("/api/v1/data/**").authenticated().and()
                .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        UserDetails admin = User.withUsername("Admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();
        UserDetails employee = User.withUsername("Employee")
                .password(passwordEncoder().encode("employee"))
                .roles("EMPLOYEE")
                .build();
        UserDetails importer = User.withUsername("Importer")
                .password(passwordEncoder().encode("importer"))
                .roles("IMPORTER")
                .build();

        return new InMemoryUserDetailsManager(admin, employee, importer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


