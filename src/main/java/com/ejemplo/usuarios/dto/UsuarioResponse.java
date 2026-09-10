package com.ejemplo.usuarios.dto;

import com.ejemplo.usuarios.entity.EstadoUsuario;
import com.ejemplo.usuarios.entity.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private EstadoUsuario estado;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.estado = usuario.getEstado();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }
}
