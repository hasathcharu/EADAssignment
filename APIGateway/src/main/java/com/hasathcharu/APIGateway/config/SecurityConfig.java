package com.hasathcharu.APIGateway.config;


import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;


@Configuration
public class SecurityConfig{

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwks;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/eureka/**")
                        .permitAll()
                        .pathMatchers("/discovery")
                        .permitAll()
                        .pathMatchers("/actuator/**")
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2->oauth2.jwt(jwt->jwt.jwtDecoder(customDecoder())));
        return http.build();
    }

    private ReactiveJwtDecoder customDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri("https://api.asgardeo.io/t/hasathcharu/oauth2/jwks")
                .jwtProcessorCustomizer(customizer->customizer.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("at+jwt"))))
                .build();
    }

//    private JwtDecoder customDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri("https://api.asgardeo.io/t/hasathcharu/oauth2/jwks")
//                .jwtProcessorCustomizer(jwtCustomizer -> jwtCustomizer.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("at+jwt")))).build();
//    }


    //    @Bean
//    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/message/**").hasAuthority("SCOPE_message:read")
//                        .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.decoder()));
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        System.out.println(jwks);
//        System.out.println("Hello");
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize->authorize.anyRequest().authenticated())
//                .oauth2ResourceServer((oauth2)->oauth2.jwt(jwt->jwt.decoder(customDecoder())));
//        return http.build();
//    }
}
