package toyprojects.to_do_list;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class TestSecurityConfig {

    @Bean(name = "testSecurityFilterChain")
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // .authorizeHttpRequests(authorize -> authorize
                //         .anyRequest().permitAll())
                // .csrf(csrf -> csrf.disable());
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests
                    // .requestMatchers(HttpMethod.POST, "/todo").authenticated())
                    .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(Customizer.withDefaults())
                );
        return http.build();
    }
}
