package com.ejemplo.usuarios.dto;

import com.ejemplo.usuarios.entity.Usuario;

public record UserResponse(
    Long id,
    String nombre,
    String email,
    String estado
) {
    public static UserResponse fromEntity(Usuario usuario) {
        return new UserResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getEstado()
        );
    }
}