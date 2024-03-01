package kb.persondata.configuration.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthorizationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    @Value("${spring.jwt.auth.converter.principal-attribute}")
    private String principalClaimName;
    @Value("${spring.jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwtSource) {
        Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwtSource).stream(),
                extractResourceRoles(jwtSource).stream()
        ).collect(Collectors.toSet()
        );
        return new JwtAuthenticationToken(
                jwtSource,
                authorities,
                getPrincipalName(jwtSource)
        );
    }

    private String getPrincipalName(Jwt jwtSource) {
        String claimName = JwtClaimNames.SUB;
        if (principalClaimName != null) {
            claimName = principalClaimName;
        }

        return jwtSource.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwtSource) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (jwtSource.getClaim("resource_access") == null) {
            return Set.of();
        }

        resourceAccess = jwtSource.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return Set.of();
        }

        resource = (Map<String, Object>) resourceAccess.get(resourceId);
        resourceRoles = (Collection<String>) resource.get("roles");

        return resourceRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

    }
}
