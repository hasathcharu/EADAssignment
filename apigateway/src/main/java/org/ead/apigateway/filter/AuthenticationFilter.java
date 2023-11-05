package org.ead.apigateway.filter;

import org.ead.apigateway.exception.RestException;
import org.ead.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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
            System.out.println("HIHIHI");
            System.out.println(exchange.getRequest().getMethod());
            if (validator.isSecured.test(exchange.getRequest())){
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RestException(HttpStatus.UNAUTHORIZED, "Missing Token");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader!=null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }try {
                    jwtUtil.validateToken(authHeader);
                }catch (Exception e){
                    System.out.println("Invalid access");
                    throw new RestException(HttpStatus.UNAUTHORIZED,"Invalid Token");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config{

    }
}
