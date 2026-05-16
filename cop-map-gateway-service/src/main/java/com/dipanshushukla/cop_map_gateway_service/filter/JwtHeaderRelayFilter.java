package com.dipanshushukla.cop_map_gateway_service.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderRelayFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Let auth/ws bypass the JWT logic completely
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/ws")) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .filter(principal -> principal instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> {
                    // Extract specific claims
                    String badgeNumber = jwt.getClaimAsString("badgeNumber");
                    String role = jwt.getClaimAsString("role");
                    String thanaId = jwt.getClaimAsString("thanaId");

                    // Mutate the request to add them as secure internal headers
                    ServerHttpRequest mutated = exchange.getRequest()
                            .mutate()
                            .header("X-Badge-Number", badgeNumber)
                            .header("X-Role", role)
                            .header("X-Thana-Id", thanaId)
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(chain.filter(exchange)); // No JWT found
    }
}