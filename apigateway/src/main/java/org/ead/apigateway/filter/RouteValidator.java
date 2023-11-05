package org.ead.apigateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;


@Component
public class RouteValidator {

    public static final Map<String, List<HttpMethod>> openApiEndpoints = new HashMap<>();
    static {
        openApiEndpoints.put("/api/v1/auth/authenticate", Arrays.asList(HttpMethod.POST));
        openApiEndpoints.put("/api/v1/auth/register", Arrays.asList(HttpMethod.GET, HttpMethod.POST));
        openApiEndpoints.put("/discovery", Arrays.asList(HttpMethod.GET));
        openApiEndpoints.put("/api/inventory", Arrays.asList(HttpMethod.GET));
        openApiEndpoints.put("/api/inventory/{PID}", Arrays.asList(HttpMethod.GET));
        openApiEndpoints.put("/api/user", Arrays.asList(HttpMethod.GET));
    }

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();

        if (openApiEndpoints.containsKey(path)) {
            return !openApiEndpoints.get(path).contains(requestMethod);
        }

        return true;
    };
}
