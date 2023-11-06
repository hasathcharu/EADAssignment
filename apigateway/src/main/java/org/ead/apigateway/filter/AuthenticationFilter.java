package org.ead.apigateway.filter;

import io.jsonwebtoken.Claims;
import org.ead.apigateway.exception.RestException;
import org.ead.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            //check it contains the header or not. if yes -> validate the token
            if (!validator.isOpen.test(exchange.getRequest())){
                System.out.println("Protected Route");
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RestException(HttpStatus.UNAUTHORIZED, "Missing Token");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader!=null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }
                try {
                    exchange.getRequest().mutate().header("X-User-Email", jwtUtil.extractUsername(authHeader));
                    jwtUtil.validateToken(authHeader);
                }catch (Exception e){
                    throw new RestException(HttpStatus.UNAUTHORIZED,"Invalid Token");
                }
                List<String> roles = jwtUtil.extractRoles(authHeader);
                if(validator.isUserOnly.test(exchange.getRequest()) && !roles.contains("USER")){
                    System.out.println("Not a user");
                    throw new RestException(HttpStatus.UNAUTHORIZED,"Insufficient Privileges. You need to be a user.");
                }
                if(validator.isAdminOnly.test(exchange.getRequest()) && !roles.contains("ADMIN")){
                    System.out.println("Not an admin");
                    throw new RestException(HttpStatus.UNAUTHORIZED,"Insufficient Privileges. You need to be an admin.");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config{

    }
}
