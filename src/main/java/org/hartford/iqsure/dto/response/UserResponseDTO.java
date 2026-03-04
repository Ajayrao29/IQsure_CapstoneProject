package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Integer userPoints;
    private String role;
}

