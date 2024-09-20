package toyprojects.to_do_list;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    @Override
    public SecurityContext createSecurityContext(WithMockJwt mockJwt) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create a mock JWT with the `sub` claim
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", mockJwt.subject())
                .build();

        // Create an Authentication token with the JWT
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, null, null);
        context.setAuthentication(authentication);

        return context;
    }
}

