package org.ead.apigateway.filter;

import org.ead.apigateway.dto.AuthorizationResponse;
import org.ead.apigateway.exception.RestException;
import org.ead.apigateway.models.Role;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;


    public AuthenticationFilter(RouteValidator validator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
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
                if(authHeader == null){
                    throw new RestException(HttpStatus.UNAUTHORIZED, "Missing Header");
                }
                return webClientBuilder.build()
                        .get()
                        .uri("http://identitymanagement/api/auth/public/authorize")
                        .header("Authorization",authHeader)
                        .retrieve()
                        .bodyToMono(AuthorizationResponse.class)
                        .onErrorResume(e -> {
                            System.out.println(e.getMessage());
                            if(e.getMessage().contains("404 Not Found")){
                                throw new RestException(HttpStatus.NOT_FOUND, "User not found");
                            }
                            if(e.getMessage().contains("401")){
                                throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid Token");
                            }
                            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to identity management");
                        })
                        .flatMap(result -> {
                            // Handle the result asynchronously
                            Collection<Role> roles = result.getRoles();
                            String email = result.getEmail();
                            if(validator.isUserOnly.test(exchange.getRequest()) && !roles.contains(Role.USER)){
                                System.out.println("Not a user");
                                return Mono.error(new RestException(HttpStatus.UNAUTHORIZED,"Insufficient Privileges. You need to be a user."));
                            }
                            if(validator.isDelivererOnly.test(exchange.getRequest()) && !roles.contains(Role.DELIVERER)){
                                System.out.println("Not a Deliverer");
                                return Mono.error(new RestException(HttpStatus.UNAUTHORIZED,"Insufficient Privileges. You need to be a Deliverer."));
                            }
                            if(validator.isAdminOnly.test(exchange.getRequest()) && !roles.contains(Role.ADMIN)){
                                System.out.println("Not an admin");
                                return Mono.error(new RestException(HttpStatus.UNAUTHORIZED,"Insufficient Privileges. You need to be an admin."));
                            }
                            System.out.println("Hello");
                            exchange.getRequest().mutate().header("X-User-Email", email);
                            return chain.filter(exchange);
                        });
            }
            else{
                return chain.filter(exchange);
            }
        });
    }

    public static class Config{

    }
}
