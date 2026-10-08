package com.ejemplo.usuarios.service;

import com.ejemplo.usuarios.dto.*;
import com.ejemplo.usuarios.entity.Usuario;
import com.ejemplo.usuarios.exception.*;
import com.ejemplo.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias por constructor
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("El email '" + request.email() + "' ya está registrado.");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        Usuario nuevoUsuario = new Usuario(request.nombre(), request.email(), passwordHash, "ACTIVO");

        return UserResponse.fromEntity(usuarioRepository.save(nuevoUsuario));
    }

    public UserResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        return UserResponse.fromEntity(usuario);
    }

    public List<UserResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return UserResponse.fromEntity(usuario);
    }

    public UserResponse actualizar(Long id, UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (!usuario.getEmail().equalsIgnoreCase(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("El email '" + request.email() + "' ya está registrado por otro usuario.");
        }

        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());

        return UserResponse.fromEntity(usuarioRepository.save(usuario));
    }

    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setEstado("INACTIVO");
        usuarioRepository.save(usuario);
    }
}