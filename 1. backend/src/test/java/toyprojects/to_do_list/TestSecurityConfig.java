package toyprojects.to_do_list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import toyprojects.to_do_list.security.SecurityConfig;

@Configuration
// @EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class TestSecurityConfig {

    @Autowired
    private SecurityConfig config;

    @Bean(name = "testSecurityFilterChain")
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                    .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                    .decoder(config.jwtDecoder())
                    .jwtAuthenticationConverter(config.jwtAuthenticationConverter())
                    )
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}


