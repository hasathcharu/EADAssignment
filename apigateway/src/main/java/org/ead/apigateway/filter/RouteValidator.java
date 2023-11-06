package org.ead.apigateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;


@Component
public class RouteValidator {

    public static final Map<String, List<HttpMethod>> publicRoutes = new HashMap<>();
    static {
        publicRoutes.put("/api/auth/public/authenticate", Arrays.asList(HttpMethod.POST));
        publicRoutes.put("/api/auth/public/register", Arrays.asList(HttpMethod.POST));
        publicRoutes.put("/api/auth/admin/", Arrays.asList(HttpMethod.DELETE));
        publicRoutes.put("/discovery", Arrays.asList(HttpMethod.GET));
        publicRoutes.put("/api/inventory", Arrays.asList(HttpMethod.GET));
        publicRoutes.put("/api/inventory/{PID}", Arrays.asList(HttpMethod.GET));
    }
    public static final Map<String, List<HttpMethod>> userRoutes = new HashMap<>();
    static {
        publicRoutes.put("/api/user", Arrays.asList(HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE));
        publicRoutes.put("/api/order/", Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE));
    }

    public static final Map<String, List<HttpMethod>> adminRoutes = new HashMap<>();
    static {
        publicRoutes.put("/api/user", Arrays.asList(HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE));
        publicRoutes.put("/api/order/", Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE));
    }



    public Predicate<ServerHttpRequest> isOpen = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();

        if (publicRoutes.containsKey(path)) {
            return !publicRoutes.get(path).contains(requestMethod);
        }
        return true;
    };

    public Predicate<ServerHttpRequest> isUserOnly = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();

        if (userRoutes.containsKey(path)) {
            return !userRoutes.get(path).contains(requestMethod);
        }
        return true;
    };

    public Predicate<ServerHttpRequest> isAdminOnly = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();

        boolean found = adminRoutes.keySet().stream()
                .anyMatch(path::startsWith);

        if (found) {
            return !adminRoutes.get(path).contains(requestMethod);
        }
        return true;
    };



}
