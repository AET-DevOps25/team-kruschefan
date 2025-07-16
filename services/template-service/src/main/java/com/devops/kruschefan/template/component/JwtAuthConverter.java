package com.devops.kruschefan.template.component;

import com.nimbusds.jwt.JWTClaimNames;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;


    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                        jwtGrantedAuthoritiesConverter
                                .convert(jwt)
                                .stream(),
                        extractResourceRoles(jwt)
                                .stream()
                )
                .collect(Collectors.toSet());
        JwtAuthenticationToken token = new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt)
        );
        return token;
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JWTClaimNames.SUBJECT;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }

    // private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
    //     Map<String, Object> resourceAccess;
    //     Map<String, Object> resource;
    //     Collection<String> resourceRoles;
    //     if (jwt.getClaim("resource_access") == null) {
    //         return Set.of();
    //     }
    //     resourceAccess = jwt.getClaim("resource_access");
    //     if (resourceAccess.get(resourceId) == null) {
    //         return Set.of();
    //     }
    //     resource = (Map<String, Object>) resourceAccess.get(resourceId);
    //     resourceRoles = (Collection<String>) resource.get("roles");
    //     return resourceRoles
    //             .stream()
    //             .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
    //             .collect(Collectors.toSet());
    // }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1. Extract roles from the realm_access claim
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
            authorities.addAll(realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet()));
        }

        // 2. Extract roles from the resource_access claim
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (jwt.getClaim("resource_access") != null) {
            resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess.containsKey(resourceId)) {
                resource = (Map<String, Object>) resourceAccess.get(resourceId);
                if (resource.containsKey("roles")) {
                    resourceRoles = (Collection<String>) resource.get("roles");
                    authorities.addAll(resourceRoles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toSet()));
                }
            }
        }
        
        return authorities;
    }
}
