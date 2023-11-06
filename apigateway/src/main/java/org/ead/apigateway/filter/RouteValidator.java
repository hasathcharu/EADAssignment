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
        publicRoutes.put("/api/auth/public/", Arrays.asList(HttpMethod.POST));
        publicRoutes.put("/discovery", Arrays.asList(HttpMethod.GET));
        publicRoutes.put("/api/inventory/public", Arrays.asList(HttpMethod.GET));
        publicRoutes.put("/api/inventory/system", Arrays.asList(HttpMethod.PUT));
    }
    public static final Map<String, List<HttpMethod>> userRoutes = new HashMap<>();
    static {
        userRoutes.put("/api/user/basic", Arrays.asList(HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE));
        userRoutes.put("/api/order/basic", Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE));
    }

    public static final Map<String, List<HttpMethod>> adminRoutes = new HashMap<>();
    static {
        adminRoutes.put("/api/user/admin", Arrays.asList(HttpMethod.GET, HttpMethod.DELETE));
        adminRoutes.put("/api/order/admin", Arrays.asList(HttpMethod.GET, HttpMethod.PUT));
        adminRoutes.put("/api/inventory/admin", Arrays.asList(HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE));
    }

    public Predicate<ServerHttpRequest> isOpen = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();
        String key = getMatch(path, publicRoutes);
        if (key!=null) {
            return publicRoutes.get(key).contains(requestMethod);
        }
        return false;
    };

    private String getMatch(String path, Map<String, List<HttpMethod>> routes) {
        return routes.keySet()
                .stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);
    }
    public Predicate<ServerHttpRequest> isUserOnly = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();
        String key = getMatch(path, userRoutes);
        if (key!=null) {
            return userRoutes.get(key).contains(requestMethod);
        }
        return false;
    };

    public Predicate<ServerHttpRequest> isAdminOnly = request -> {
        String path = request.getURI().getPath();
        HttpMethod requestMethod = request.getMethod();
        String key = getMatch(path, adminRoutes);
        if (key!=null) {
            return adminRoutes.get(key).contains(requestMethod);
        }
        return false;
    };



}
