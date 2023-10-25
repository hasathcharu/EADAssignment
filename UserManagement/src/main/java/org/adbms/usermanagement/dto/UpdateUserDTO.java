package org.adbms.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateUserDTO {
    private String name;
    private String email;
    private String gender;
    private String telephone;
    private String address;
}
