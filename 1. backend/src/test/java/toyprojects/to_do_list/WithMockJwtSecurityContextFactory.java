package toyprojects.to_do_list;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    
    @Override
    public SecurityContext createSecurityContext(WithMockJwt mockJwt) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create a mock JWT with the `sub` and `scope` claims
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", mockJwt.subject())
                .claim("scope", mockJwt.scope()) 
                .issuer("https://ra8bitegg-dev.us.auth0.com/")
                .build();

        // Convert the scope claim into granted authorities
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);

        // Create an Authentication token with the JWT and authorities
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

        context.setAuthentication(authentication);

        return context;
    }
}

