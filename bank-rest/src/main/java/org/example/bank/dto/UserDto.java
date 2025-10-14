package org.example.bank.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private boolean enabled;
    private OffsetDateTime createdAt;
    private Set<RoleDto> roles;
}
