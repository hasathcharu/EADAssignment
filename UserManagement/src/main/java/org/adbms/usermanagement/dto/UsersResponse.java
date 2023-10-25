package org.adbms.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UsersResponse {
    private String id;
    private String name;
    private String email;
    private String gender;
    private String telephone;
    private String address;
}
