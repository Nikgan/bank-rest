package org.example.bank.mapper;

import org.example.bank.dto.RoleDto;
import org.example.bank.dto.UserDto;
import org.example.bank.model.Role;
import org.example.bank.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());

        Set<RoleDto> roles = user.getRoles().stream()
                .map(UserMapper::roleToDto)
                .collect(Collectors.toSet());
        dto.setRoles(roles);

        return dto;
    }

    private static RoleDto roleToDto(Role role) {
        if (role == null) return null;

        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }
}
