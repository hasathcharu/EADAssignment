package org.ead.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ead.apigateway.models.Role;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationResponse {
    private String email;
    private Collection<Role> roles;
}
